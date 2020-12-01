package tech.pod.game.generics.controller.core;

@FunctionalInterface
public interface Action<G extends GameController<?, ?, ?>>
{
    void apply(G controller);
}
