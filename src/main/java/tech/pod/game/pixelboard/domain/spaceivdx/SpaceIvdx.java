package tech.pod.game.pixelboard.domain.spaceivdx;


import java.util.Date;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tech.pod.game.generics.engine.EngineConfiguration;
import tech.pod.game.generics.engine.GameEngine;
import tech.pod.game.generics.entity.td.MappedTDGrid;
import tech.pod.game.generics.entity.td.TDGrid;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDPosition;
import tech.pod.game.generics.ui.graphics.GameImage;
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

/**
 * Embed the SpaceIvx game logic.
 */
public class SpaceIvdx extends GameEngine
{
    private static final String UI_CONFIGURATION = "screen_configuration";
    private static final String BACKGROUND = "background";

    private final Map<Class<? extends TDMaterial>, Function<TDMaterial, TDImage<RGBAColor>>> images;
    private final JFrameScreen<RGBAColor> screen;
    private final Function<TDGrid, GameImage<RGBAColor>> screenConverter;

    public SpaceIvdx(EngineConfiguration configuration) {
        super(configuration, new SpaceIvdxController());
        this.images = UIFacade.spaceIvdxImages();
        this.screenConverter = UIFacade.rgbaScreenConverter(this.configuration.get(BACKGROUND), images);
        this.screen = new JFrameScreen<>(
                this.configuration.get(UI_CONFIGURATION),
                RGBAColor::toPixel,
                this.configuration.get(BACKGROUND)
        );
        this.screen.addKeyListener(new UIFacade.SpaceIdvxKeyListener(this.getController()));
        this.configureController();
    }

    private void configureController() {
        this.getController().addSubscriber(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;
            private long timeStamp = new Date().getTime();

            @Override
            public void onSubscribe(Flow.Subscription subscription)
            {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(TDGrid item)
            {
                try {
                    var duration = new Date().getTime() - this.timeStamp;
                    if (duration < 50) {
                        Thread.sleep(50 - duration);
                    }
                    this.timeStamp = new Date().getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SpaceIvdx.this.screen.draw(SpaceIvdx.this.screenConverter.apply(item));
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable)
            {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete()
            {
                // Nothing to do
            }
        });
    }

    private SpaceIvdxController getController() {
        return (SpaceIvdxController)this.controller;
    }

    /**
     * Init the grid and its the materials.
     */
    @Override
    public void init()
    {
        // 0. Create the grid
        var grid = MappedTDGrid.of(800, 20, 1280, 20);

        // 1. Add the ship
        var shipImage = images.get(Ship.class).apply(null);
        var ship = SpaceIvdxEntityGenerator
                .generateShip(shipImage.width, shipImage.height, Missile.WIDTH, Missile.HEIGHT, 1280, 800);
        grid.add(ship);

        // 2. Add the enemies
        var enemyImage = images.get(Enemy.class).apply(null);
        var enemyStartPosition = TDPosition.of(10, 10);
        SpaceIvdxEntityGenerator
                .newEnemiesStream(
                        enemyImage.width, enemyImage.height, Missile.WIDTH, Missile.HEIGHT, 3, 10, enemyStartPosition
                )
                .forEach(grid::add);

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
        this.screen.close();
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
        this.getController().addUpdateEvent(SpaceIvdxControllerFacade.gameUpdater);
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
