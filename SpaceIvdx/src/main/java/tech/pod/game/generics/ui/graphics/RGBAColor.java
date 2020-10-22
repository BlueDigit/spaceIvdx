package tech.pod.game.generics.ui.graphics;

/**
 * And RGB color is based
 */
public class RGBAColor extends Color
{
    private int alpha;
    private int red;
    private int blue;
    private int gold;

    public RGBAColor(String name, int alpha, int red, int gold, int blue) {
        super(name);
        this.alpha = RGBAColor.checkValue("alpha", alpha);
        this.red = RGBAColor.checkValue("red", red);
        this.blue = RGBAColor.checkValue("blue", blue);
        this.gold = RGBAColor.checkValue("gold", gold);
    }

    public int getAlpha() {
        return alpha;
    }

    public int getRed() {
        return red;
    }

    public int getBlue() {
        return blue;
    }

    public int getGold() {
        return gold;
    }

    public int toPixel() {
        return (this.alpha << 24) + (this.red << 16) + (this.gold << 8) + this.blue;
    }

    private static int checkValue(String color, int value) {
        if (value < 0 || 256 <= value) {
            throw new IllegalArgumentException(String.format("RGBAColor: %s should be between 0 and 256", color));
        }
        return value;
    }
}
