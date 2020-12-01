package tech.pod.game.generics.entity.core;

public interface Movable<P extends Position, M extends Material<P, M>>
{
    P lastPosition();
    int countMoves();
}
