package tech.pod.game.generics.controller.core;

import tech.pod.game.generics.entity.core.Grid;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Position;
import tech.pod.game.generics.ui.graphics.Color;
import tech.pod.game.generics.ui.graphics.GameScreen;

/**
 * TODO: the controller should have no state about coming instructions.
 * @param <P>
 * @param <C>
 * @param <E>
 */
public interface GameController<P extends Position, C extends Color, E extends Material<P, E>>
{
    void end();
    boolean ended();
    Grid<P, E> getGrid();
    GameScreen<C> getScreen();
    GameController<P, C, E> updateUI();
    GameController<P, C, E> updateStates();
    GameController<P, C, E> executeActions();
    GameController<P, C, E> addAction(Action<GameController<P, C, E>> action);
    GameController<P, C, E> executeAction(Action<GameController<P, C, E>> action);
    GameController<P, C, E> addUIUpdateAction(Action<GameController<P, C, E>> action);
}
