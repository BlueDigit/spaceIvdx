package tech.pod.game.generics.ui.graphics;

public enum RGBADefinedColors
{
    BLANK(0, 0, 0, 0),
    BLACK(255, 0, 0, 0),
    LOW_YELLOW(0, 0, 255, 0),
    MID_YELLOW(127, 0, 255, 0),
    HEAVY_YELLOW(255, 0, 255, 0);

    private final RGBAColor color;

    RGBADefinedColors(int alpha, int red, int gold, int blue) {
        this.color = new RGBAColor(alpha, red, gold, blue);
    }

    public RGBAColor color() {
        return this.color;
    }
}
