package tech.pod.game.generics.ui.graphics;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    protected final Function<List<C>, Stream<C>> streamer;

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
        this(width, height, backgroundColor, List::stream);
    }

    public TDImage(int width, int height, C backgroundColor, Function<List<C>, Stream<C>> streamer) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("TDImage: height or with < 0");
        }
        this.backgroundColor = Objects.requireNonNull(backgroundColor, "TDImage: Null background");
        this.streamer = Objects.requireNonNull(streamer, "TDImage: Null streamer");
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
     * Set a color (pixel) at a specific position in the image array.
     * <br><br>
     * Previous color will be erased if exist.
     * @param i The pixel position in the image array
     * @param color The color to define
     * @return This
     */
    public TDImage<C> setColor(int i, C color) {
        if (i < this.matrix.size()) {
            this.matrix.set(i, color);
        }
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

    /**
     * Set the color at a specific cell into the image.
     *
     * The class simulate here a two dimensional array for the calling code. The method will internally compute
     * a one dimension location from the two dimensional location and call {@link TDImage#setColor(int, Color)}.
     *
     * Does not set the pixel if its position is over bounding.
     *
     * @param x should be >= 0
     * @param y should be >= 0
     * @param color The color to set a this position. Should not be null.
     * @return this
     * @see TDImage#setColor(int, Color)
     */
    public TDImage<C> setColor(int x, int y, C color) {
        if (x < this.width && y < this.height) {
            int idx = TDImage.computeIndex(x, y, this.width, this.height);
            return this.setColor(idx, color);
        }
        return this;
    }

    public Optional<C> getColor(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return Optional.empty();
        }
        int idx = TDImage.computeIndex(x, y, this.width, this.height);
        return Optional.of(this.getColor(idx));
    }

    @Override
    public Stream<C> stream() {
        return this.streamer.apply(this.matrix);
    }

    public static int computeIndex(int x, int y, int width, int height) {
        if (x >= width || x < 0 || y >= height || y < 0) {
            throw new IllegalArgumentException();
        }
        return (y * width) + x;
    }

    public static <C extends Color> TDImage<C> copy(TDImage<C> toCopy) {
        var copy = new TDImage<>(toCopy.width, toCopy.height, toCopy.backgroundColor, toCopy.streamer) {};
        IntStream
            .range(0, toCopy.size)
            .forEach(i -> copy.setColor(i, toCopy.getColor(i)));
        return copy;
    }
}