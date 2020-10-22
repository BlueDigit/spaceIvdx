package tech.pod.game.generics.entity;

import java.util.List;

public class TwoDimensionalEra implements Era<TwoDimensionPosition>
{
    private final TwoDimensionPosition upperLeft;
    private final TwoDimensionPosition lowerRight;
    private final int era;

    private TwoDimensionalEra(TwoDimensionPosition upperLeft, TwoDimensionPosition lowerRight)
    {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
        this.era = 0;
    }

    @Override
    public List<TwoDimensionPosition> getPositions()
    {
        return List.of(this.upperLeft, this.lowerRight);
    }

    @Override
    public int era()
    {
        return this.era;
    }

    public static TwoDimensionalEra of(TwoDimensionPosition upperLeft, TwoDimensionPosition lowerRight)
    {
        return new TwoDimensionalEra(upperLeft, lowerRight);
    }

    public static int computeEra(TwoDimensionPosition upperLeft, TwoDimensionPosition lowerRight)
    {
        return (lowerRight.x - upperLeft.x) * (lowerRight.y - lowerRight.x);
    }
}
