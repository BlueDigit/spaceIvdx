package tech.pod.game.generics.engine;

import java.util.HashMap;
import java.util.Map;

public class EngineConfiguration
{
    private final Map<String, Object> configuration = new HashMap<>();

    public EngineConfiguration put(String param, Object value) {
        this.configuration.put(param, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String param) {
        return (T)this.configuration.get(param);
    }
}
