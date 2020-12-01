package tech.pod.game.pixelboard.domain.spaceivdx.entity;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Movable;
import tech.pod.game.generics.entity.core.Shooter;
import tech.pod.game.generics.entity.core.Vector;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDPosition;

/**
 * In spaceIvdx a ship represents an instance of the allied vessel.
 * <br><br>
 * A ship can shot a {@link Missile} and moved by a {@link tech.pod.game.generics.entity.core.Vector}.
 * @see ShipMissile
 * @see Ship#laser
 */
public class Ship extends TDMaterial implements Shooter<Missile>, Movable<TDPosition, TDMaterial>
{
    /** The ship's laser */
    private final Function<Ship, Shooter<ShipMissile>> laser;
    private TDPosition lastPosition;
    private AtomicReference<Integer> moveCounter = new AtomicReference<>(0);

    /**
     * Avoid rogue code to instantiate this class. Make the calling code use the static methods.
     * @param comparator should not be null
     * @see TDMaterial#of(TDPosition, TDPosition)
     */
    protected Ship(Comparator<Material<TDPosition, TDMaterial>> comparator,
                   Function<Ship, Shooter<ShipMissile>> laser)
    {
        super(comparator);
        this.laser = Objects.requireNonNull(laser, "Ship: null laser");
    }

    /**
     * Apply the {@link Ship#laser} to this and make it shoot a {@link ShipMissile}.
     * @return A new instance of ShipMissile
     */
    @Override
    public synchronized ShipMissile shoot()
    {
        return this.laser.apply(this).shoot();
    }

    @Override
    public synchronized TDMaterial translate(Vector<TDPosition, TDMaterial> vector)
    {
        this.lastPosition = this.upperLeft;
        this.moveCounter.getAndAccumulate(1, Integer::sum);
        return super.translate(vector);
    }

    @Override
    public TDPosition lastPosition()
    {
        return lastPosition;
    }

    @Override
    public int countMoves()
    {
        return this.moveCounter.get();
    }

    @Override
    public TDMaterial spawn()
    {
        var copy = Ship.of(this.upperLeft, this.lowerRight, this.laser);
        copy.moveCounter = new AtomicReference<>(this.moveCounter.get());
        copy.lastPosition = this.lastPosition;
        return copy;
    }

    /**
     * Represent a missile launch from a {@link Ship}.
     */
    public static class ShipMissile extends Missile {
        protected ShipMissile() {
            super(TDMaterial.BASIC_COMPARATOR);
        }

        @Override
        public TDMaterial spawn()
        {
            return ShipMissile.of(this.upperLeft, this.lowerRight);
        }

        public static ShipMissile of(TDPosition upperLeft, TDPosition lowerRight) {
            return (ShipMissile)new ShipMissile().setUpperLeft(upperLeft).setLowerRight(lowerRight);
        }
    }

    /**
     * Construct a new instance of ship based on its positions and laser.
     * @param upperLeft should not be null
     * @param lowerRight should not be null
     * @param laser should not be null
     * @return A new instance of {@link Ship}
     */
    public static Ship of(TDPosition upperLeft, TDPosition lowerRight, Function<Ship, Shooter<ShipMissile>> laser)
    {
        return (Ship) new Ship(TDMaterial.BASIC_COMPARATOR, laser).setUpperLeft(upperLeft).setLowerRight(lowerRight);
    }
}
