package tech.pod.game.generics.engine;

import java.util.Date;
import java.util.Objects;
import tech.pod.game.generics.controller.core.GameController;

/**
 * Loop based game engine.
 * <br><br>
 * Embed the minimal logic needed to run a game.
 * <br><br>
 * This class is a template method pattern that leave three methods definition to the implementation:
 * <ol>
 *     <li>The init method: used to initialize the game, usually used to configure a game controller</li>
 *     <li>The loop action method: used to declare some actions at each loop iteration</li>
 *     <li>The reset method: reset the game</li>
 * </ol>
 * The init method is called at the beginning of each game. The loop action method is called during the game play,
 * at each loop iteration. The reset method is normally called by the implementation itself when the loop has been
 * escaped in order to reset the game or some other states.
 * <br><br>
 * Each game loop is manage by the {@link GameEngine#loopAction()} method.
 */
public abstract class GameEngine
{
    protected final GameController<?> controller;
    protected EngineConfiguration configuration;

    public GameEngine(EngineConfiguration configuration, GameController<?> controller)
    {
        this.configuration = Objects.requireNonNull(configuration, "GameEngine: configuration");
        this.controller = Objects.requireNonNull(controller, "GameEngine: null controller");
    }

    /**
     * Execute the loop.
     * <ol>
     *     <li>The {@link GameEngine#init()} is called</li>
     *     <li>The result of {@link GameController#ended()} defines the loop invariant</li>
     *     <li>The {@link GameEngine#loopAction()} method is called</li>
     *     <li>Actions are executed against the controller, it's states are updated and a submit is proceed</li>
     *     <li>A minimal loop duration is computed</li>
     * </ol>
     */
    public void run()
    {
        this.init();
        var timeStamp = new Date().getTime();
        while(!this.controller.ended()) {
            this.loopAction();
            this.controller
                    .executeActions()
                    .updateStates()
                    .submitGrid();
            try {
                var duration = new Date().getTime() - timeStamp;
                if (duration < 120) {
                    Thread.sleep(120 - duration);
                }
                timeStamp = new Date().getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop()
    {
        this.controller.end();
    }

    public abstract void init();

    public abstract void reset();

    public abstract void loopAction();
}
