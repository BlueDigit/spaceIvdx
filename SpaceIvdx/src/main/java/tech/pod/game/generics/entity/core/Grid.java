package tech.pod.game.generics.entity.core;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represent a grid composed by {@link Material} covering the same type of {@link Position}.
 * @param <P> The type of position to handle
 */
public interface Grid<P extends Position, E extends Material<P, E>/*, G extends Grid<P, E, G>*/> extends Material<P, E>
{
    /**
     * Check if the grid contains a specific material
     * @param material Should not be null
     * @return True if the grid contains this material or false otherwise
     */
    boolean contains(Material<P, E> material);

    /**
     * Add a grid object to the grid.
     * @param material Should not be null
     * @return this
     */
    Grid<P, E> add(Material<P, E> material);

    /**
     * Remove one specific material from the grid
     * @param material should not be null
     * @return The removed material
     */
    Grid<P, E> remove(Material<P, E> material);

    /**
     * Locate all material at a precise position in the grid.
     * @param position should not be null
     * @return A set of all materials at that exact position.Can be empty if no materials has been found at that
     *         position.
     */
    List<Material<P, E>> get(P position);

    /**
     * Locate all materials int a certain cell of the grid.
     * @param position Should not be null
     * @return A set of all materials at that position. Can be empty if no materials has been found at the position.
     */
    TreeSet<Material<P, E>> getFromCell(P position);

    Material<P, E> translate(P position, Vector<P, E> vector);

    <O extends Material<P, E>> Material<P, E> translate(Class<O> jazz, Vector<P, E> vector);

    /**
     * Retrieve a list of materials.
     * @param jazz The implementation class of the Material to retrieve. Should not be null.
     * @param <O> The type ouf the output.
     * @return A list of material of an empty list if the type is not referenced by the grid
     */
    <O extends Material<P, E>> TreeSet<O> getFromCell(Class<O> jazz);

    /**
     * Stream all materials held by the grid and sorted by position.
     * @return A stream of materials
     */
    Stream<Material<P, E>> stream();

    /**
     * Sorted stream
     * @param comparator should not be null
     * @return A stream of materials
     */
    Stream<Material<P, E>> stream(Comparator<Material<P, E>> comparator);

    Function<P, Optional<P>> getHashFunctionByPosition();

    Function<E, List<P>> getHashFunctionByMaterial();
}
