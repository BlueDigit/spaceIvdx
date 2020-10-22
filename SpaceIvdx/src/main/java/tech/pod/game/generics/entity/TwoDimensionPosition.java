package tech.pod.game.generics.entity;

public class TwoDimensionPosition implements Position
{
    final int x;
    final int y;

    private TwoDimensionPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static TwoDimensionPosition of(int x, int y) {
        return new TwoDimensionPosition(x, y);
    }
}
