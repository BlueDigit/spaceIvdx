package tech.pod.game.generics.engine;

import java.util.Objects;
import tech.pod.game.generics.controller.GameController;

public abstract class GameEngine
{
    protected final GameController<?, ?> controller;
    protected EngineConfiguration configuration;

    public GameEngine(EngineConfiguration configuration, GameController<?, ?> controller) {
        this.configuration = Objects.requireNonNull(configuration, "GameEngine: configuration");
        this.controller = Objects.requireNonNull(controller, "GameEngine: null controller");
    }

    public void run() {
        while(!this.controller.ended()) {
            this.controller
                    .readActions()
                    .executeActions()
                    .computeCollisions()
                    .updateStates()
                    .updateUI();
        }
    }

    public void stop() {
        this.controller.end();
    }
}
