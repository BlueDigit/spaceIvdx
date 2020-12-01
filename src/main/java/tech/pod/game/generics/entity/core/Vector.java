package tech.pod.game.generics.entity.core;

@FunctionalInterface
public interface Vector<P extends Position, E extends Material<P, E>>
{
    E apply(E material);
}
