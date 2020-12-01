package tech.pod.game.generics.controller.core;

import java.util.function.Consumer;
import tech.pod.game.generics.entity.core.Grid;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Position;
import tech.pod.game.generics.ui.graphics.Color;
import tech.pod.game.generics.ui.graphics.GameScreen;

/**
 * TODO: the controller should not hold states about upcoming instructions.
 * @param <P>
 * @param <C>
 * @param <E>
 */
public interface GameController<P extends Position, C extends Color, E extends Material<P, E>>
{
    void end();
    void restart();
    boolean ended();
    void userEnd();
    boolean userEnded();
    Grid<P, E> getGrid();
    GameScreen<C> getScreen();
    GameController<P, C, E> updateUI();

    GameController<P, C, E> addStateUpdater(Consumer<GameController<P, C, E>> stateUpdate);
    GameController<P, C, E> updateStates();

    /**
     * Add an action to the inner flow of actions.
     * @param action should not be null
     * @return this
     * @see GameController#executeActions()
     */
    GameController<P, C, E> addAction(Action<GameController<P, C, E>> action);

    /**
     * Execute all the actions added to the execution queue.
     *
     * The method implementation should remove all executed action from the inner action flow structure.
     *
     * The actions are executed synchronously one after each other. If concurrent execution is needed here then
     * action should provide its own mechanism to do so.
     *
     * @return this
     */
    GameController<P, C, E> executeActions();

    /**
     * Should execute an action asynchronously.
     * @param action should not be null
     * @return this
     */
    GameController<P, C, E> executeAction(Action<GameController<P, C, E>> action);

    GameController<P, C, E> addUIUpdateAction(Action<GameController<P, C, E>> action);
}
