package tech.pod.game.pixelboard.domain.spaceivdx.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import tech.pod.game.generics.controller.td.TDGridController;
import tech.pod.game.generics.entity.core.Vector;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDMoves;
import tech.pod.game.generics.entity.td.TDPosition;
import tech.pod.game.generics.ui.graphics.GameImage;
import tech.pod.game.generics.ui.graphics.RGBAColor;
import tech.pod.game.generics.ui.graphics.RGBADefinedColors;
import tech.pod.game.generics.ui.graphics.TDImage;
import tech.pod.game.generics.ui.graphics.TDRGBAImage;
import tech.pod.game.generics.ui.graphics.file.TDRGBAImageToPngSerializer;
import tech.pod.game.generics.utils.Tuple;
import tech.pod.game.pixelboard.domain.spaceivdx.controller.SpaceIvdxController;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Enemy;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Ship;

/**
 * Utils methods for SpaceIvdx images
 */
public class UIFacade
{
    /**
     * Make this class static.
     */
    private UIFacade() {
        // Avoid rogue code to instantiate this class
    }

    public static Map<Class<? extends TDMaterial>, Function<TDMaterial, TDImage<RGBAColor>>> spaceIvdxImages()
    {
        var imageSerializer = new TDRGBAImageToPngSerializer();

        // Load enemies images
        var enemiesResources = List
                .of("Enemy1A.png", "Enemy1B.png", "Enemy1C.png",
                    "Enemy2A.png", "Enemy2B.png", "Enemy2C.png",
                    "Enemy3A.png", "Enemy3B.png", "Enemy3C.png")
                .stream()
                .map(rscName -> {
                    var stream = UIFacade.class.getClassLoader().getResourceAsStream(rscName);
                    return Tuple.of(rscName, stream);
                })
                .collect(Collectors.toList());
        var enemyImages = imageSerializer.deserializeStreams(enemiesResources);

        // Load missile image
        var missileImage = UIFacade.missileImage();

        // Load ship image
        var shipResource = UIFacade.class.getClassLoader().getResourceAsStream("Craft.png");
        var shipImage = imageSerializer.deserialize(shipResource);

        // Create the image map.
        return Map
                .of(Enemy.class, m -> {
                        if (m == null) {
                            return enemyImages.get(enemiesResources.get(0).l);
                        } else {
                            var enemy = (Enemy) m;
                            int order = enemy.ordinal() % 3;
                            int nbMoves = enemy.countMoves() % 3;
                            int imageIdx = (order * 3) + nbMoves % 3;
                            return enemyImages.get(enemiesResources.get(imageIdx).l);
                        }
                    },
                    Enemy.EnemyMissile.class, m -> missileImage,
                    Ship.ShipMissile.class, m -> missileImage,
                    Ship.class, m -> shipImage);
    }

    public static TDImage<RGBAColor> missileImage()
    {
        return new TDRGBAImage(5, 10, RGBADefinedColors.MID_YELLOW.color(), false);
    }

    public static Function<TDGridController<RGBAColor>, GameImage<RGBAColor>> rgbaScreenConverter(
            TDImage<RGBAColor> backGround,
            Map<Class<? extends TDMaterial>, Function<TDMaterial, TDImage<RGBAColor>>> images)
    {
        return gController -> {
            TDImage<RGBAColor> outputImage = TDImage.copy(backGround);
            gController.getGrid().stream().forEach(material -> {
                var objectImage = images.get(material.getClass()).apply(material.get());
                material.get().streamPositions().forEach(tdPosition -> {
                    if (gController.getGrid().isInCollision(TDMaterial.of(tdPosition, tdPosition))) {
                        int x = tdPosition.x - material.get().getUpperLeft().x;
                        int y = tdPosition.y - material.get().getUpperLeft().y;
                        objectImage
                                .getColor(x, y)
                                .filter(color -> !RGBADefinedColors.BLANK.color().equals(color))
                                .ifPresent(color -> outputImage.setColor(tdPosition.x, tdPosition.y, color));
                    }
                });
            });
            return outputImage;
        };
    }

    public static class SpaceIdvxKeyListener implements KeyListener
    {
        private final Object lock = new Object();
        private final SpaceIvdxController controller;
        private final Thread controllerThread;
        private Optional<Vector<TDPosition, TDMaterial>> vector = Optional.empty();

        public SpaceIdvxKeyListener(SpaceIvdxController controller) {
            this.controller = Objects.requireNonNull(controller, "SpaceIdxCControllerFacade: null controller");
            this.controllerThread = new Thread(() -> {
                long start = new Date().getTime();
                while (true) {
                    synchronized (this.lock) {
                        vector.ifPresent(
                                action -> controller.executeAction(
                                        control -> control.getGrid().translate(Ship.class, action)));
                    }
                    long duration = (new Date().getTime()) - start;
                    if (duration < 25) {
                        try {
                            Thread.sleep(25 - duration);
                        } catch (Exception e) {/* Nothing to do*/}
                    }
                    start = new Date().getTime();
                }
            });
            this.controllerThread.start();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Nothing to do
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            synchronized (this.lock) {
                this.vector = switch (keyCode) {
                    case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> Optional.empty();
                    default -> this.vector;
                };
            }
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            int keyCode = e.getKeyCode();
            synchronized (this.lock) {
                this.vector = switch (keyCode) {
                    case KeyEvent.VK_LEFT -> Optional.of(tdMaterial -> {
                        if (tdMaterial.getUpperLeft().x - 5 >= 0) {
                            return TDMoves.LEFT.computeVector(5).apply(tdMaterial);
                        }
                        return tdMaterial;
                    });
                    case KeyEvent.VK_RIGHT -> Optional.of(tdMaterial -> {
                        if (tdMaterial.getLowerRight().x + 5 < this.controller.getGrid().get().getLowerRight().x) {
                            return TDMoves.RIGHT.computeVector(5).apply(tdMaterial);
                        }
                        return tdMaterial;
                    });
                    case KeyEvent.VK_SPACE -> this.shotAction();
                    case KeyEvent.VK_ESCAPE -> {
                        this.controller.userEnd();
                        yield Optional.empty();
                    }
                    default -> this.vector;
                };
            }
        }

        Optional<Vector<TDPosition, TDMaterial>> shotAction() {
            Vector<TDPosition, TDMaterial> action = tdMaterial -> {
                if (tdMaterial instanceof Ship s) {
                    var missile = s.shoot();
                    this.controller.getGrid().add(missile);
                }
                return tdMaterial;
            };
            this.controller.executeAction(control -> control.getGrid().translate(Ship.class, action));
            return this.vector;
        }
    }
}
