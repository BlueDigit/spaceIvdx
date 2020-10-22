package tech.pod.game.generics.controller;

import tech.pod.game.generics.entity.Position;
import tech.pod.game.generics.ui.graphics.Color;

@FunctionalInterface
public interface Action<P extends Position, C extends Color>
{
    void apply(GameController<P, C> controller);
}
