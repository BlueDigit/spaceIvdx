package tech.pod.game.generics.entity;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represent a grid composed by {@link GridObject} covering the same type of {@link Position}.
 * @param <P> The type of position to handle
 */
public interface Grid<P extends Position> extends GridObject<P>
{
    /**
     * Add a grid object to the grid.
     * @param gridObject Should not be null
     * @return this
     */
    Grid<P> add(GridObject<P> gridObject);

    /**
     * Search a position from within the grid
     * @param position
     * @return
     */
    Optional<GridObject<P>> get(P position);
    Optional<GridObject<P>> get(Era<P> era);
    <E extends GridObject<P>> Optional<GridObject<P>> get(Class<E> jazz);
    Stream<GridObject<P>> stream();
    Function<Position, Integer> getHashFunction();
}
