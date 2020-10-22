package tech.pod.game.generics.ui.graphics;

import java.util.stream.Stream;

/**
 * Represent an image.
 *
 * An image should be represented by a law level data structure somewhere in its life cycle. This interface should
 * offer the necessary behavior in order to manipulate this low level data structure.
 *
 * Such a structure is typically represented with a byte array but an implementation would like to handle
 * more complex structures like multidimensional arrays of even maps. Implementing this class should help to
 * the calling code to apply effect and convert its inner structure to a byte array representation.
 */
public interface GameImage<C extends Color>
{
    /**
     * A standardized representation of the inner data structure.
     *
     * The inner data structure should expose the colors handled by
     * the implementation.
     *
     * @return The representation of the inner data structure
     */
    Stream<C> stream();
}
