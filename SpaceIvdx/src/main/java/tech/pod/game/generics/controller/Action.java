package tech.pod.game.generics.controller;

import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Position;
import tech.pod.game.generics.ui.graphics.Color;

@FunctionalInterface
public interface Action<P extends Position, C extends Color, E extends Material<P, E>>
{
    void apply(GameController<P, C, E> controller);
}
