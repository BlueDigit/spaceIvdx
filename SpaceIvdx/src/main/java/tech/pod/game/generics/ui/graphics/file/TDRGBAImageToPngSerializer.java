package tech.pod.game.generics.ui.graphics.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import tech.pod.game.generics.ui.graphics.RGBAColor;
import tech.pod.game.generics.ui.graphics.RGBADefinedColors;
import tech.pod.game.generics.ui.graphics.TDRGBAImage;
import tech.pod.game.generics.utils.Tuple;

public class TDRGBAImageToPngSerializer implements ImageFileSerializer<TDRGBAImage>
{
    private TDRGBAImage deserialize(BufferedImage bImage)
    {
        // 0. Init the variables
        int width = bImage.getWidth();
        int height = bImage.getHeight();
        int[] tab = bImage.getRGB(0, 0, width, height, null, 0, width);
        var counter = new AtomicInteger(0);

        // 2. Create the image
        return Arrays
                .stream(tab)
                .mapToObj(pixel -> {
                    var idx = counter.getAndAccumulate(1, Integer::sum);
                    var color = new RGBAColor(
                            pixel >> 24 & 0x000000FF,
                            pixel >> 16 & 0x000000FF,
                            pixel >> 8 & 0x000000FF,
                            pixel & 0x000000FF
                    );
                    return Tuple.of(idx, color);
                })
                .reduce(new TDRGBAImage(bImage.getWidth(), bImage.getHeight(), RGBADefinedColors.BLANK.color(), true),
                        (image, pair) -> (TDRGBAImage) image.setColor(pair.l, pair.r),
                        (l, r) -> {
                            IntStream.range(0, r.size).forEach(idx -> l.setColor(idx, r.getColor(idx)));
                            return l;
                        });
    }

    @Override
    public TDRGBAImage deserialize(String fileName)
    {
        // 0. Check entry
        Objects.requireNonNull(fileName, "TDRGBAImageToPngSerializer: null file name");

        // 1. Read the byte from files.
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
            return this.deserialize(image);
        } catch (Exception e) {
            // TODO: clean this
            throw new RuntimeException(e);
        }
    }

    @Override
    public TDRGBAImage deserialize(InputStream fileStream)
    {
        // 0. Check entry
        Objects.requireNonNull(fileStream, "TDRGBAImageToPngSerializer: null file name");

        // 1. Read the byte from files.
        try {
            BufferedImage image = ImageIO.read(fileStream);
            return this.deserialize(image);
        } catch (Exception e) {
            // TODO: clean this
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, TDRGBAImage> deserialize(List<String> fileNames)
    {
        return Objects
                .requireNonNull(fileNames, "TDTGBAImageToPngSerializer: null file names")
                .stream()
                .map(fileName -> Tuple.of(fileName, this.deserialize(fileName)))
                .collect(Collectors.toMap(t -> t.l, t -> t.r));
    }

    @Override
    public Map<String, TDRGBAImage> deserializeStreams(List<Tuple.Pair<String, InputStream>> fileStreams)
    {
        return Objects
                .requireNonNull(fileStreams, "TDRGBAImageToPngSerializer: null files streams")
                .stream()
                .map(pair -> Tuple.of(pair.l, this.deserialize(pair.r)))
                .collect(Collectors.toMap(t -> t.l, t -> t.r));
    }

    @Override
    public void serialize(String fileName)
    {
        throw new UnsupportedOperationException("Not implement yet");
    }
}
