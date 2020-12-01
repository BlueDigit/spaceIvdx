package tech.pod.game.pixelboard.domain.spaceivdx.entity;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import tech.pod.game.generics.entity.td.TDPosition;

public class SpaceIvdxEntityGenerator
{
    /**
     * Make this class static.
     */
    private SpaceIvdxEntityGenerator() {
        // Avoid rogue code to instantiate this class
    }

    public static Ship generateShip(
            int width, int height, int missileWidth, int missileHeight, int boardWidth, int boardHeight)
    {
        var upperLeft = TDPosition.of(boardWidth / 2 - (width / 2), boardHeight - height);
        var lowerRight = TDPosition.of(upperLeft.x + width, boardHeight);
        return Ship.of(
                upperLeft,
                lowerRight,
                ship -> () -> {
                    var x = (ship.getLowerRight().x - ship.getUpperLeft().x)
                             / 2
                             - (missileWidth / 2)
                             + ship.getUpperLeft().x;
                    var missileUpperLeft = TDPosition
                            .of(x, ship.getUpperLeft().y - missileHeight);
                    var missileLowerRight = TDPosition
                            .of(x + missileWidth, ship.getUpperLeft().y);
                    return Ship.ShipMissile.of(missileUpperLeft, missileLowerRight);
                });
    }

    public static Enemy generateEnemy(int width, int height, int missileWidth, int missileHeight)
    {
        var upperLeft = TDPosition.of(10, 10);
        var lowerRight = TDPosition.of(width + 10, height + 10);
        return Enemy.of(
                upperLeft,
                lowerRight,
                enemy -> () -> {
                    var x = (enemy.getLowerRight().x - enemy.getUpperLeft().x)
                            / 2
                            - (missileWidth / 2)
                            + enemy.getUpperLeft().x;
                    var missileUpperLeft = TDPosition
                            .of(x, enemy.getLowerRight().y - missileHeight);
                    var missileLowerRight = TDPosition
                            .of(x + missileWidth, enemy.getLowerRight().y);
                    return Enemy.EnemyMissile.of(missileUpperLeft, missileLowerRight);
                });
    }

    public static Stream<Enemy> newEnemiesStream(
            int width, int height, int missileWidth, int missileHeight, int rows, int cols, TDPosition startPosition)
    {
        // This double loop is acceptable for a small range of materials. This should be avoided with large amount
        // of object and a recursive concurrent approach should be preferred.
        var lowerLeft = TDPosition.of(startPosition.x + width, startPosition.y + width);
        return IntStream
                .range(0, cols)
                .mapToObj(x -> IntStream
                          .range(0, rows)
                          .mapToObj(y -> {
                              var enemy = generateEnemy(width, height, missileWidth, missileHeight);
                              enemy.setUpperLeft(
                                      TDPosition.of(startPosition.x + (width * x) + 20 * x,
                                                    startPosition.y + (height * y) + 20 * y)
                              );
                              enemy.setLowerRight(
                                      TDPosition.of(lowerLeft.x + (width * x) + 20 * x,
                                                    lowerLeft.y + (height * y) + 20 * y)
                              );
                              return enemy;
                          })
                )
                .flatMap(s -> s);
    }
}
