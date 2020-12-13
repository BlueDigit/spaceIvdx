package tech.pod.game.generics.controller.core;

import tech.pod.game.generics.entity.core.Grid;

@FunctionalInterface
public interface Action<G extends Grid<?, ?>>
{
    void apply(G Grid);
}
