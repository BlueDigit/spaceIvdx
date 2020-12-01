package tech.pod.game.generics.controller.td;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.pod.game.generics.controller.core.Action;
import tech.pod.game.generics.controller.core.GameController;
import tech.pod.game.generics.entity.td.TDGrid;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDPosition;
import tech.pod.game.generics.entity.td.TDTestEntityGenerator;
import tech.pod.game.generics.entity.td.TDVector;
import tech.pod.game.generics.ui.graphics.Color;
import tech.pod.game.generics.ui.graphics.ColorConverter;
import tech.pod.game.generics.ui.graphics.GameImage;
import tech.pod.game.generics.ui.graphics.JFrameScreen;
import tech.pod.game.generics.ui.graphics.RGBAColor;
import tech.pod.game.generics.ui.graphics.TDImage;
import tech.pod.game.generics.ui.graphics.UIConfiguration;

class TDGridControllerTest
{
    private TDGrid grid;
    private ScreenMock<RGBAColor> screen;
    private final RGBAColor black = new RGBAColor(255, 0, 0, 0);
    private final RGBAColor yellow = new RGBAColor(255, 0, 255, 0);
    private TDImage<RGBAColor> blackBackGround;

    private static class ScreenMock<C extends Color> extends JFrameScreen<C> {
        TDImage<C> image;

        public ScreenMock(
                UIConfiguration configuration,
                ColorConverter<C, Integer> colorConverter,
                GameImage<C> backGround) {
            super(configuration, colorConverter, backGround);
        }

        @Override
        public ScreenMock<C> draw(GameImage<C> image) {
            if (!(image instanceof TDImage)) {
                throw new ClassCastException();
            }
            this.image = (TDImage<C>)image;
            return this;
        }
    }

    @BeforeEach
    void init() {
        this.grid = TDGrid.of(100, 5, 100, 5);
        //TODO: TDMaterial<E extends TDMaterial<E>> implements Material<TDPosition, TDMaterial<E>>
        Supplier<TDMaterial> enemyGenerator = () -> new TDTestEntityGenerator.Enemy()
                .setUpperLeft(TDPosition.of(0, 0))
                .setLowerRight(TDPosition.of(10, 10));
        TDTestEntityGenerator
                .generateMaterialsByTranslation(enemyGenerator, TDVector.of(10, 10), 15)
                .forEach(this.grid::add);

        var configuration = new UIConfiguration(100, 100);
        this.blackBackGround = new TDImage<>(configuration.getWidth(), configuration.getHeight(), black) {
            @Override
            public Stream<RGBAColor> stream()
            {
                return IntStream.range(0, this.getSize()).mapToObj(this::getColor);
            }
        };
        this.screen = new ScreenMock<>(configuration, RGBAColor::toPixel, blackBackGround);
    }

    @Test
    @DisplayName("Executing actions should move materials and update the screen")
    void executingActionsShouldWork()
    {
        // setup controller
        final var controller = new TDGridController<RGBAColor>()
                .setGrid(this.grid)
                .setScreen(this.screen, gController -> {
                    TDImage<RGBAColor> image = TDImage.copy(this.blackBackGround);
                    this.grid.stream().forEach(m -> m.get().streamPositions().forEach(p -> {
                        if (gController.getGrid().isInCollision(TDMaterial.of(p, p))) {
                            image.setColor(p.x, p.y, yellow);
                        }
                    }));
                    return image;
                });

        // declare action
        Action<GameController<TDPosition, RGBAColor, TDMaterial>> action = gController -> gController
                .getGrid()
                .translate(TDTestEntityGenerator.Enemy.class,
                           enemy -> TDVector.of(1, 1).apply(enemy));

        // Check that the controller does move materials and copy the sprite to the pixels image
        final int[] counter = new int[1];
        final Map<TDMaterial, TDPosition> positionMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            controller.addAction(action);
            controller.executeActions();
            controller.updateUI();
            controller.getGrid().stream().forEach(m -> {
                m.get().streamPositions().forEach(p -> {
                    if (controller.getGrid().isInCollision(TDMaterial.of(p, p))) {
                        counter[0] += 1;
                        var color = this.screen.image.getColor(p.x, p.y);
                        Assertions.assertTrue(color.isPresent());
                        Assertions.assertEquals(yellow, color.get());
                    }
                });
                if (positionMap.containsKey(m.get())) {
                    Assertions.assertNotEquals(m.get().getUpperLeft(), positionMap.get(m.get()));
                }
                positionMap.put(m.get(), m.get().getUpperLeft());
            });
        }
        Assertions.assertTrue(counter[0] > 0);
        Assertions.assertFalse(positionMap.isEmpty());
    }
}