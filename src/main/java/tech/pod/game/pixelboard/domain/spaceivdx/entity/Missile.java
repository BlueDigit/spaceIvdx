package tech.pod.game.pixelboard.domain.spaceivdx.entity;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Movable;
import tech.pod.game.generics.entity.core.Vector;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDPosition;

public abstract class Missile extends TDMaterial implements Movable<TDPosition, TDMaterial>
{
    protected TDPosition lastPosition;
    protected AtomicReference<Integer> moveCounter = new AtomicReference<>(0);

    public static final int HEIGHT = 10;
    public static final int WIDTH = 5;

    /**
     * Avoid rogue code to instantiate this class. Make the calling code use the static methods.
     *
     * @param comparator should not be null
     * @see TDMaterial#of(TDPosition, TDPosition)
     */
    protected Missile(Comparator<Material<TDPosition, TDMaterial>> comparator)
    {
        super(comparator);
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
        return this.lastPosition == null ? this.upperLeft : this.lastPosition;
    }

    @Override
    public int countMoves()
    {
        return this.moveCounter.get();
    }
}
