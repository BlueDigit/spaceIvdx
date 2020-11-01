package tech.pod.game.generics.entity.td;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Vector;

public class TDMaterial implements Material<TDPosition, TDMaterial>
{
    protected TDPosition upperLeft;
    protected TDPosition lowerRight;
    protected final Comparator<TDMaterial> comparator;

    protected static final BiPredicate<TDMaterial, TDMaterial> CROSS_CHECK = (l, r) -> {
        if (l.upperLeft.y <= r.upperLeft.y && r.upperLeft.y <= l.lowerRight.y) {
            return (l.upperLeft.x <= r.upperLeft.x && r.upperLeft.x <= l.lowerRight.x);
        }
        return false;
    };

    protected static final Comparator<TDMaterial> BASIC_COMPARATOR = (l, r) -> {
        var ulComp = l.upperLeft.compareTo(r.upperLeft);
        if (ulComp == 0) {
            return l.lowerRight.compareTo(r.lowerRight);
        }
        return ulComp;
    };

    /**
     * Avoid rogue code to instantiate this class. Make the calling code use the static methods.
     * @see TDMaterial#of(TDPosition, TDPosition)
     */
    protected TDMaterial(Comparator<TDMaterial> comparator)
    {
        this.comparator = Objects.requireNonNull(comparator, "TDMaterial: null comparator");
    }

    public TDMaterial setUpperLeft(TDPosition upperLeft)
    {
        this.upperLeft = Objects.requireNonNull(upperLeft, "TwoDGameObject: null upperLeft");
        return this;
    }

    public TDMaterial setLowerRight(TDPosition lowerRight)
    {
        this.lowerRight = Objects.requireNonNull(lowerRight, "TwoDGameObject: null lowerRight");
        return this;
    }

    @Override
    public boolean isInCollision(Material<TDPosition, TDMaterial> other)
    {
        return TDMaterial.CROSS_CHECK.test(this, other.get()) || TDMaterial.CROSS_CHECK.test(other.get(), this);
    }

    @Override
    public TDMaterial spawn()
    {
        return new TDMaterial(this.comparator).setLowerRight(this.lowerRight)
                                              .setUpperLeft(this.upperLeft);
    }

    @Override
    public synchronized TDMaterial translate(Vector<TDPosition, TDMaterial> vector)
    {
        var translation = vector.apply(this);
        this.upperLeft = translation.upperLeft;
        this.lowerRight = translation.lowerRight;
        return this;
    }

    @Override
    public synchronized List<TDPosition> computePositions()
    {
        return IntStream
                .range(this.upperLeft.y, this.lowerRight.y)
                .mapToObj(y -> IntStream
                        .range(this.upperLeft.x, this.lowerRight.x)
                        .mapToObj(x -> TDPosition.of(x, y))
                )
                .flatMap(s -> s)
                .collect(Collectors.toList());
    }

    @Override
    public TDMaterial get()
    {
        return this;
    }

    @Override
    public int compareTo(TDMaterial o)
    {
        return this.comparator.compare(this, o);
    }

    public static TDMaterial of(TDPosition upperLeft, TDPosition lowerRight) {
        return TDMaterial.of(upperLeft, lowerRight, TDMaterial.BASIC_COMPARATOR);
    }

    public static TDMaterial of(TDPosition upperLeft, TDPosition lowerRight, Comparator<TDMaterial> comparator) {
        return new TDMaterial(comparator).setUpperLeft(upperLeft)
                                         .setLowerRight(lowerRight);
    }
}
