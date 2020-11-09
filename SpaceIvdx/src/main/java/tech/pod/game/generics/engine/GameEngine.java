package tech.pod.game.generics.engine;

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
    }

    public void run() {
        while(!this.controller.ended()) {
            this.controller
                    .executeActions()
                    .updateStates()
                    .updateUI();
        }
    }

    public void stop() {
        this.controller.end();
    }
}
