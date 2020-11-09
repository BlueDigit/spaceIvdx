package tech.pod.game.generics.entity.td;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Vector;

public class TDMaterial implements Material<TDPosition, TDMaterial>
{
    protected TDPosition upperLeft;
    protected TDPosition lowerRight;
    protected final Comparator<Material<TDPosition, TDMaterial>> comparator;

    protected static final BiPredicate<TDMaterial, TDMaterial> CROSS_CHECK = (l, r) -> {
        if (l.upperLeft.y <= r.upperLeft.y && r.upperLeft.y < l.lowerRight.y) {
            return (l.upperLeft.x <= r.upperLeft.x && r.upperLeft.x < l.lowerRight.x);
        }
        return false;
    };

    /**
     * Compare two materials based on their upper left and lower right positions.
     * <br><br>
     * If materials are same then the comparator will return 0. If all position are equals then the two materials
     * are compared based on their hash code that is computed by {@link Object#hashCode()}
     * @see TDPosition#compareTo(TDPosition)
     */
    public static final Comparator<Material<TDPosition, TDMaterial>> BASIC_COMPARATOR = (l, r) -> {
        if (l == r) {
            return 0;
        }
        var ulComp = l.get().upperLeft.compareTo(r.get().upperLeft);
        if (ulComp == 0) {
            int lrComparison = l.get().lowerRight.compareTo(r.get().lowerRight);
            return lrComparison != 0 ? lrComparison : Integer.compare(l.hashCode(), r.hashCode());
        }
        return ulComp;
    };

    /**
     * Avoid rogue code to instantiate this class. Make the calling code use the static methods.
     * @see TDMaterial#of(TDPosition, TDPosition)
     */
    protected TDMaterial(Comparator<Material<TDPosition, TDMaterial>> comparator)
    {
        this.comparator = Objects.requireNonNull(comparator, "TDMaterial: null comparator");
    }

    public TDMaterial setUpperLeft(TDPosition upperLeft)
    {
        this.upperLeft = Objects.requireNonNull(upperLeft, "TwoDGameObject: null upperLeft");
        return this;
    }

    public TDPosition getUpperLeft()
    {
        return upperLeft;
    }

    public TDMaterial setLowerRight(TDPosition lowerRight)
    {
        this.lowerRight = Objects.requireNonNull(lowerRight, "TwoDGameObject: null lowerRight");
        return this;
    }

    public TDPosition getLowerRight()
    {
        return lowerRight;
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
        return vector.apply(this);
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

    public synchronized Stream<TDPosition> streamPositions()
    {
        return IntStream
                .range(this.upperLeft.y, this.lowerRight.y)
                .mapToObj(y -> IntStream
                                  .range(this.upperLeft.x, this.lowerRight.x)
                                  .mapToObj(x -> TDPosition.of(x, y))
                         )
                .flatMap(s -> s);
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

    public static TDMaterial of(TDPosition upperLeft, TDPosition lowerRight,
                                Comparator<Material<TDPosition, TDMaterial>> comparator) {
        return new TDMaterial(comparator).setUpperLeft(upperLeft)
                                         .setLowerRight(lowerRight);
    }
}
