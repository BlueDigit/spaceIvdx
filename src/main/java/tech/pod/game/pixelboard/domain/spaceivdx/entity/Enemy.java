package tech.pod.game.pixelboard.domain.spaceivdx.entity;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Movable;
import tech.pod.game.generics.entity.core.Ordered;
import tech.pod.game.generics.entity.core.Shooter;
import tech.pod.game.generics.entity.core.Vector;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDPosition;

public class Enemy extends TDMaterial implements Shooter<Missile>, Movable<TDPosition, TDMaterial>, Ordered
{
    private final static AtomicReference<Integer> counter = new AtomicReference<>(0);

    /** The enemy's laser */
    private final Function<Enemy, Shooter<EnemyMissile>> laser;
    private int ordinal;
    private TDPosition lastPosition;
    private AtomicReference<Integer> moveCounter = new AtomicReference<>(0);
    private AtomicReference<Integer> moveDelay = new AtomicReference<>(0);

    /**
     * Avoid rogue code to instantiate this class. Make the calling code use the static methods.
     *
     * @param comparator should not be null
     * @see TDMaterial#of(TDPosition, TDPosition)
     */
    protected Enemy(Comparator<Material<TDPosition, TDMaterial>> comparator,
                    Function<Enemy, Shooter<EnemyMissile>> laser)
    {
        super(comparator);
        this.laser = Objects.requireNonNull(laser, "Ship: null laser");
        this.ordinal = Enemy.counter.getAndAccumulate(1, Integer::sum);
    }

    @Override
    public EnemyMissile shoot()
    {
        return this.laser.apply(this).shoot();
    }

    @Override
    public TDPosition lastPosition()
    {
        return this.lastPosition == null ? this.upperLeft : this.lastPosition;
    }

    @Override
    public int countMoves()
    {
        return this.moveCounter.get();
    }

    @Override
    public Enemy translate(Vector<TDPosition, TDMaterial> vector)
    {
        this.lastPosition = this.upperLeft;
        this.moveDelay.getAndAccumulate(1, Integer::sum);
        if (moveDelay.get() % 3 == 0) {
            this.moveCounter.getAndAccumulate(1, Integer::sum);
        }
        return (Enemy)vector.apply(this);
    }

    @Override
    public int ordinal()
    {
        return this.ordinal;
    }

    @Override
    public TDMaterial spawn()
    {
        var copy = Enemy.of(this.upperLeft, this.lowerRight, this.laser);
        copy.moveDelay = new AtomicReference<>(this.moveDelay.get());
        copy.moveCounter = new AtomicReference<>(this.moveCounter.get());
        return copy;
    }

    /**
     * Represent a missile launch from a {@link Ship}.
     */
    public static class EnemyMissile extends Missile {

        protected EnemyMissile() {
            super(TDMaterial.BASIC_COMPARATOR);
        }

        @Override
        public EnemyMissile spawn()
        {
            return EnemyMissile.of(this.upperLeft, this.lowerRight);
        }

        public static EnemyMissile of(TDPosition upperLeft, TDPosition lowerRight)
        {
            return (EnemyMissile) new EnemyMissile().setUpperLeft(upperLeft).setLowerRight(lowerRight);
        }

    }

    public static Enemy of(TDPosition upperLeft, TDPosition lowerRight, Function<Enemy, Shooter<EnemyMissile>> laser)
    {
        var enemy = (Enemy) new Enemy(TDMaterial.BASIC_COMPARATOR, laser)
                .setUpperLeft(upperLeft)
                .setLowerRight(lowerRight);
        enemy.lastPosition = enemy.upperLeft;
        return enemy;
    }
}
