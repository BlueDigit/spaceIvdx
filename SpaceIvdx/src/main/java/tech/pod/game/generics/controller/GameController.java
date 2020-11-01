package tech.pod.game.generics.controller;

import java.util.List;
import java.util.function.BiConsumer;
import tech.pod.game.generics.entity.core.Grid;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Position;
import tech.pod.game.generics.ui.graphics.Color;
import tech.pod.game.generics.ui.graphics.GameImage;

/**
 * TODO: the controller should have no state about coming instructions.
 * @param <P>
 * @param <C>
 * @param <E>
 */
public interface GameController<P extends Position, C extends Color, E extends Material<P, E>>
{
    boolean ended();
    void end();
    GameController<P, C, E> pushActions(Action<P, C, E> action);
    GameController<P, C, E> executeActions();
    GameController<P, C, E> computeCollisions();
    GameController<P, C, E> updateStates();
    Grid<P, E> getGrid();
    Material<P, E> getGridObject(P position);
    GameController<P, C, E> addToGrid(Material<P, E> material);
    void updateUI();
    void setUiUpdateCommands(List<BiConsumer<Material<P, E>, GameImage<C>>> uiUpdateCommands);
}
