package tech.pod.game.generics.ui.graphics;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JFrameScreenTest
{
    @Test
    @Disabled
    @DisplayName("Drawing an image should work")
    void drawingAnImageShouldWork() throws Exception
    {
        var blueBackGround = new RGBAColor("Blue", 255, 0, 0, 255);
        var configuration = new UIConfiguration(1920, 1080);
        var blueBackground = new TDImage<>(configuration.getWidth(), configuration.getHeight(), blueBackGround) {
            @Override
            public Stream<RGBAColor> stream()
            {
                return IntStream.range(0, this.getSize()).mapToObj(this::getColor);
            }
        };
        var screen = JFrameScreen.of(configuration, RGBAColor::toPixel, blueBackground);
        screen.draw();
        Thread.sleep(10000);
        screen.configure(configuration, blueBackground);
        screen.draw();
        Thread.sleep(10000);
    }
}