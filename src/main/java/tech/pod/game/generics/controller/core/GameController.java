package tech.pod.game.generics.controller.core;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Consumer;

import tech.pod.game.generics.entity.core.Grid;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Position;

/**
 * Expose the control API of a game.
 * <br><br>
 * A game is basically composed by a board materialized by {@link Grid} by which the {@link Material} exists.
 * The controller controls the events that are generated against the game and make sure that every actions
 * proceed by the engine or a user are executed in a specific order to respect the grid's integrity.
 * <br><br>
 * In a normal use case the actions and event managed by the controller should be deleted after each execution
 * that should be materialized by a call to the {@link GameController#executeActions()} method.
 * <br><br>
 *     TODO: The comment is not relevant
 * A controller should be decoupled from the view, interacting directly with it's grid. Preferred implementation
 * should make use of the {@link GameController#subscribe(Subscriber)},
 * {@link GameController#addUpdateEvent(Consumer)} and {@link GameController#submitGrid()} methods to update the
 * views connected to the game.
 * <br><br>
 * With this simple API the game controller should already do much:
 * <ul>
 *     <li>Manage grid integrity</li>
 *     <li>Manage interaction between external events and the the game board</li>
 *     <li>Publish events to IHM or other game monitoring tools</li>
 * </ul>
 * @param <G> The type of {@link Position} handled by the inner grid
 * @param <E> The type of {@link Material} handled by the inner grid
 */
public interface GameController<G extends Grid<?, ?>> extends Publisher<G>
{
    /**
     * End the game.
     */
    void end();

    /**
     * Restart the game.
     */
    void restart();

    /**
     * Check if the game is finished.
     * @return True if game is done or false otherwise
     */
    boolean ended();

    /**
     * Signal to the {@link GameController} instance that the user has terminate the game.
     */
    void userEnd();

    /**
     * Determine if the game was ended by the user.
     * @return True if so and false otherwise
     */
    boolean userEnded();

    /**
     * Return the inner grid.
     * @return Should not be null
     */
    G getGrid();

    /**
     * Update all observers.
     * @return this
     */
    GameController<G> submitGrid();

    /**
     * Add a state update event.
     * @param stateUpdate Should not be null
     * @return this
     */
    GameController<G> addUpdateEvent(Consumer<GameController<G>> stateUpdate);

    /**
     * Update all states.
     * @return this
     */
    GameController<G> updateStates();

    /**
     * Add an action to the inner flow of actions.
     * @param action should not be null
     * @return this
     * @see GameController#executeActions()
     */
    GameController<G> addAction(Action<G> action);

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
    GameController<G> executeActions();

    /**
     * Should execute an action asynchronously.
     * @param action should not be null
     * @return this
     */
    GameController<G> executeAction(Action<G> action);

    GameController<G> addSubscriber(Flow.Subscriber<G> subscriber);
}
