package tech.pod.game.generics.ui.graphics;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Two dimensional image representation.
 *
 * The image is represented internally by a one dimension list of {@link Color} not
 * exposed to the calling code.
 */
public abstract class TDImage<C extends Color> implements GameImage<C>
{
    public final int height;
    public final int width;
    public final int size;
    public final C backgroundColor;
    protected final List<C> matrix;

    /**
     * Construct an image.
     * <br><br>
     * The image size is computed as width * height. The background is constructed by copying the color at each
     * position of the inner matrix.
     * <br><br>
     * @param width should be > 0
     * @param height should be > 0
     * @param backgroundColor should not be null
     */
    public TDImage(int width, int height, C backgroundColor) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("TDImage: height or with < 0");
        }
        this.backgroundColor = Objects.requireNonNull(backgroundColor, "TDImage: Null background");
        this.width = width;
        this.height = height;
        this.size = height * width;
        this.matrix = IntStream
                .range(0, size)
                .mapToObj(i -> backgroundColor)
                .collect(Collectors.toList());
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getSize()
    {
        return size;
    }

    public C getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Define a pixel color
     * @param i The pixel position in the image array
     * @param color The color to define
     * @return This
     */
    public TDImage<C> setColor(int i, C color) {
        this.matrix.set(i, color);
        return this;
    }

    /**
     * Return the color defined at a specific index of the matrix. If no color is defined at this position then
     * the background color is returned.
     * @param i should be >= 0 and < to {@link TDImage#size}
     * @return A non null color.
     */
    public C getColor(int i) {
        return this.matrix.get(i);
    }

    public TDImage<C> setColor(int x, int y, C color) {
        int idx = TDImage.computeIndex(x, y, this.width);
        return this.setColor(idx, color);
    }

    public C getColor(int x, int y) {
        int idx = TDImage.computeIndex(x, y, this.width);
        return this.getColor(idx);
    }

    private static int computeIndex(int x, int y, int width) {
        return y * width + x;
    }

    public static int computeIndex(int x, int y, int width, int height) {
        if (x >= width || x < 0 || y >= height || y < 0) {
            throw new IllegalArgumentException();
        }
        return y * width + x;
    }
}