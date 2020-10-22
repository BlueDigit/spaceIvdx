package tech.pod.game.generics.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EngineConfigurationTest
{
    @Test
    @DisplayName("Simple put and get should work")
    void simplePutAndGetShouldWork() {
        // check with Integer
        var configuration = new EngineConfiguration()
                .put("number", 5)
                .put("Hello", "World");
        var number = new Integer[1];
        Assertions.assertDoesNotThrow(() -> number[0] = configuration.get("number"));
        Assertions.assertEquals(5, number[0]);

        // check with String
        var hello = new String[1];
        Assertions.assertDoesNotThrow(() -> hello[0] = configuration.get("Hello"));
        Assertions.assertEquals("World", hello[0]);
    }
}