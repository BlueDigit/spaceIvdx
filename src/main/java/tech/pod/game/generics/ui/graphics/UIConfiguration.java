package tech.pod.game.generics.ui.graphics;

public class UIConfiguration
{
    private final int height;
    private final int width;

    public UIConfiguration(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getSize() {
        return this.height * this.width;
    }
}
