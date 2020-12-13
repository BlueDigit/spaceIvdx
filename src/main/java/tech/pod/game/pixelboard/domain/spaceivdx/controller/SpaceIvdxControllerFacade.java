package tech.pod.game.pixelboard.domain.spaceivdx.controller;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import tech.pod.game.generics.controller.core.Action;
import tech.pod.game.generics.controller.core.GameController;
import tech.pod.game.generics.entity.td.TDGrid;
import tech.pod.game.generics.entity.td.TDMoves;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Enemy;
import tech.pod.game.pixelboard.domain.spaceivdx.entity.Ship;

public class SpaceIvdxControllerFacade
{
    // TODO: create a game context to wrap those parameters
    static TDMoves enemyLastMove = TDMoves.RIGHT;
    static int enemyShotCounter = 0;

    public static final int ENEMY_MOVE_FACTOR = 10;
    public static final int ENEMY_SHOT_FACTOR = 10;

    public static final Action<TDGrid> enemiesMoves = moveEnemies();
    public static final Action<TDGrid> enemiesShots = makeEnemiesShoot();
    public static final Action<TDGrid> enemiesMissileMoves = moveEnemiesMissile();
    public static final Action<TDGrid> shipMissileMoves = moveShipMissile();
    public static final Consumer<GameController<TDGrid>> gameUpdater = stateUpdater();

    /**
     * Make this class static.
     */
    private SpaceIvdxControllerFacade() {
        // Avoid rogue code to instantiate this class
    }

    /**
     * Compute enemies moves.
     *
     * The same vector moves all the enemies that are still in the grid. The algorithm search for a switch position
     * that would need a move change. If no such position applies while traversing the grid then the preceding move
     * is applied again.
     *
     * @return An instance of {@link Action} to apply on the controller game.
     */
    public static Action<TDGrid> moveEnemies() {
        return grid ->
            grid.getFromCell(Enemy.class)
                .stream()
                .map(enemy -> {
                    if (enemyLastMove.equals(TDMoves.RIGHT) && enemy.getLowerRight().x < grid.width - grid.cellWidth) {
                        return TDMoves.RIGHT;
                    } else if (enemyLastMove.equals(TDMoves.LEFT) && enemy.getUpperLeft().x > grid.cellWidth) {
                        return TDMoves.LEFT;
                    } else {
                        enemyLastMove = switch (enemyLastMove) {
                            case LEFT -> TDMoves.RIGHT;
                            case RIGHT -> TDMoves.LEFT;
                            default -> throw new IllegalStateException("No previous move");
                        };
                        return TDMoves.DOWN;
                    }
                })
                .filter(TDMoves.DOWN::equals)
                .findFirst()
                .or(() -> Optional.of(enemyLastMove))
                .ifPresent(move -> grid.translate(Enemy.class, move.computeVector(ENEMY_MOVE_FACTOR)));
    }

    public static Action<TDGrid> makeEnemiesShoot() {
        return grid -> {
            if (enemyShotCounter == 0) {
                enemyShotCounter = 8;
                var enemies = grid.getFromCell(Enemy.class);
                enemies.stream()
                       .reduce((l, r) -> l.getUpperLeft().y > r.getUpperLeft().y ? l : r)
                       .map(Enemy::getLowerRight)
                       .map(lower -> {
                           var shooters = enemies
                                   .stream()
                                   .filter(e -> e.getLowerRight().y == lower.y)
                                   .collect(Collectors.toList());
                           return shooters.get(new Random().nextInt(shooters.size()));
                       })
                       .ifPresent(shooter -> grid.add(shooter.shoot()));
            }
            --enemyShotCounter;
        };
    }

    public static Action<TDGrid> moveEnemiesMissile() {
        return grid -> grid
                .translate(Enemy.EnemyMissile.class,
                           TDMoves.DOWN.computeVector(SpaceIvdxControllerFacade.ENEMY_SHOT_FACTOR));
    }

    public static Action<TDGrid> moveShipMissile() {
        return grid -> grid.translate(Ship.ShipMissile.class, TDMoves.UP.computeVector(20));
    }

    public static Consumer<GameController<TDGrid>> stateUpdater() {
        return controller -> {
            var grid = controller.getGrid();
            grid.getFromCell(Ship.ShipMissile.class)
                .stream()
                .flatMap(shipMissile -> shipMissile
                        .computePositions()
                        .stream()
                        .flatMap(tdPosition -> grid.getFromCell(tdPosition).stream())
                        .filter(shipMissile::isInCollision)
                        .filter(tdMaterial -> tdMaterial instanceof Enemy)
                )
                .forEach(grid::remove);

            if (grid.getFromCell(Enemy.class).isEmpty()) {
                controller.end();
                return;
            }

            grid.getFromCell(Enemy.EnemyMissile.class)
                .stream()
                .flatMap(enemyMissile -> enemyMissile
                        .computePositions()
                        .stream()
                        .flatMap(tdPosition -> grid.getFromCell(tdPosition).stream())
                        .filter(enemyMissile::isInCollision)
                        .filter(tdMaterial -> tdMaterial instanceof Ship)
                )
                .findFirst()
                .ifPresent(s -> controller.end());
        };
    }
}
