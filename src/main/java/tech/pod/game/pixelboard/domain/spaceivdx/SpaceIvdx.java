package tech.pod.game.pixelboard.domain.spaceivdx;


import java.util.stream.IntStream;
import java.util.stream.Stream;
import tech.pod.game.generics.engine.EngineConfiguration;
import tech.pod.game.generics.engine.GameEngine;
import tech.pod.game.generics.entity.td.TDGrid;
import tech.pod.game.generics.entity.td.TDPosition;
import tech.pod.game.generics.ui.graphics.JFrameScreen;
import tech.pod.game.generics.ui.graphics.RGBAColor;
import tech.pod.game.generics.ui.graphics.RGBADefinedColors;
import tech.pod.game.generics.ui.graphics.TDImage;
import tech.pod.game.generics.ui.graphics.UIConfiguration;
import tech.pod.game.pixelboard.domain.spaceivdx.controller.SpaceIvdxController;
import tech.pod.game.pixelboard.domain.spaceivdx.controller.SpaceIvdxControllerFacade;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Enemy;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Missile;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Ship;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.SpaceIvdxEntityGenerator;
import tech.pod.game.pixelboard.domain.spaceivdx.ui.UIFacade;

public class SpaceIvdx extends GameEngine
{
    private static final String UI_CONFIGURATION = "screen_configuration";
    private static final String BACKGROUND = "background";

    public SpaceIvdx(EngineConfiguration configuration) {
        super(configuration, new SpaceIvdxController());
    }

    private SpaceIvdxController getController() {
        return (SpaceIvdxController)this.controller;
    }

    @Override
    public void init()
    {
        // 0. Init images
        var images = UIFacade.spaceIvdxImages();

        // 1. Init the screen
        if (this.getController().getScreen() == null) {
            var screen = new JFrameScreen<>(
                    this.configuration.get(UI_CONFIGURATION),
                    RGBAColor::toPixel,
                    this.configuration.get(BACKGROUND)
            );
            var screenConverter = UIFacade.rgbaScreenConverter(this.configuration.get(BACKGROUND), images);
            this.getController().setScreen(screen, screenConverter);

            // 2. Add the listener for user events
            screen.addKeyListener(new UIFacade.SpaceIdvxKeyListener(this.getController()));
        }

        // 3. Add the materials
        var grid = TDGrid.of(800, 20, 1280, 20);

        var shipImage = images.get(Ship.class).apply(null);
        var ship = SpaceIvdxEntityGenerator
                .generateShip(shipImage.width, shipImage.height, Missile.WIDTH, Missile.HEIGHT, 1280, 800);

        var enemyImage = images.get(Enemy.class).apply(null);
        var enemyStartPosition = TDPosition.of(10, 10);
        SpaceIvdxEntityGenerator
                .newEnemiesStream(
                        enemyImage.width, enemyImage.height, Missile.WIDTH, Missile.HEIGHT, 3, 10, enemyStartPosition
                )
                .forEach(grid::add);

        grid.add(ship);
        this.getController().setGrid(grid);
    }

    @Override
    public void reset()
    {
        // 0. Reset
        this.getController().restart();

        // 1. Init again
        this.init();
    }

    public void close()
    {
        this.getController().getScreen().close();
    }

    @Override
    public void loopAction()
    {
        // Add loop actions
        this.getController().addAction(SpaceIvdxControllerFacade.enemiesMoves);
        this.getController().addAction(SpaceIvdxControllerFacade.shipMissileMoves);
        this.getController().addAction(SpaceIvdxControllerFacade.enemiesShots);
        this.getController().addAction(SpaceIvdxControllerFacade.enemiesMissileMoves);

        // Add state updaters
        this.getController().addStateUpdater(SpaceIvdxControllerFacade.gameUpdater);
    }

    public static void main(String[] args)
    {
        // 0. Init game configuration
        var configuration = new EngineConfiguration();

        // 1. Configure the screen
        var uiConfiguration = new UIConfiguration(1280, 800);
        var background = new TDImage<>(1280, 800, RGBADefinedColors.BLACK.color()) {
            @Override
            public Stream<RGBAColor> stream() {
                return IntStream.range(0, this.getSize()).mapToObj(this::getColor);
            }
        };
        configuration.put(UI_CONFIGURATION, uiConfiguration);
        configuration.put(BACKGROUND, background);

        // 2. Launch the game
        var spaceIdx = new SpaceIvdx(configuration);
        while(!spaceIdx.getController().userEnded()) {
            spaceIdx.run();
            spaceIdx.reset();
        }

        // 3. Close the screen
        spaceIdx.close();
    }
}
