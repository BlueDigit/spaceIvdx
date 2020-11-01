package tech.pod.game.generics.entity.core;

import java.util.List;

public interface Material<P extends Position, E extends Material<P, E>> extends Comparable<E>
{
    boolean isInCollision(Material<P, E> other);
    Material<P, E> spawn();
    Material<P, E> translate(Vector<P, E> vector);
    List<P> computePositions();
    E get();
}