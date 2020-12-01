package tech.pod.game.generics.engine;

import java.util.Date;
import java.util.Objects;
import tech.pod.game.generics.controller.core.GameController;

/**
 * TODO: the controller should have no state about coming instructions.
 * TODO: the game flow should entirely be controller by the engine.
 */
public abstract class GameEngine
{
    protected final GameController<?, ?, ?> controller;
    protected EngineConfiguration configuration;

    public GameEngine(EngineConfiguration configuration, GameController<?, ?, ?> controller) {
        this.configuration = Objects.requireNonNull(configuration, "GameEngine: configuration");
        this.controller = Objects.requireNonNull(controller, "GameEngine: null controller");
        this.init();
    }

    public void run() {
        var timeStamp = new Date().getTime();
        while(!this.controller.ended()) {
            this.loopAction();
            this.controller
                    .executeActions()
                    .updateStates()
                    .updateUI();
            try {
                var duration = new Date().getTime() - timeStamp;
                if (duration < 140) {
                    Thread.sleep(140 - duration);
                }
                timeStamp = new Date().getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.controller.end();
    }

    public abstract void init();

    public abstract void reset();

    public abstract void loopAction();
}
