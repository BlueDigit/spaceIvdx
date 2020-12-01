package tech.pod.game.generics.ui.graphics;

/**
 * Two dimensions RGBA Image representation
 */
public class TDRGBAImage extends TDImage<RGBAColor>
{
    private final boolean filterBlank;

    /**
     * Construct an two dimensional RGBA image.
     * <br><br>
     * The image size is computed as width * height. The background is constructed by copying the color at each
     * position of the inner matrix.
     *
     * @param width           should be > 0
     * @param height          should be > 0
     * @param backgroundColor should not be null
     * @param filterBlank the class will filter blank color if true
     */
    public TDRGBAImage(int width, int height, RGBAColor backgroundColor, boolean filterBlank) {
        super(width, height, backgroundColor);
        this.filterBlank = filterBlank;
    }

    @Override
    public TDRGBAImage setColor(int x, int y, RGBAColor color) {
        if (this.filterBlank && RGBADefinedColors.BLANK.color().equals(color)) {
            return this;
        }
        super.setColor(x, y, color);
        return this;
    }
}
