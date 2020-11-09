package tech.pod.game.generics.ui.graphics;

import java.util.Objects;

/**
 * Typing interface for the color hierarchy.
 *
 * This interface should help the calling code to decouple the Color representation
 * from the {@link GameImage} logic. By using this interface then the GameImage hierarchy
 * could implements its own computation logic, regardless the graphics technology used
 * by the application.
 *
 */
public abstract class Color
{
    // TODO remove the color name
    private String name;

    public Color(String name) {
        this.name = Objects.requireNonNull(name, "Color: null name");
    }

    public String toString() {
        return this.name;
    }
}
