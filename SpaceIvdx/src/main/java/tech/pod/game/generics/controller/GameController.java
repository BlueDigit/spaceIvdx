package tech.pod.game.generics.controller;

import java.util.List;
import java.util.function.BiConsumer;
import tech.pod.game.generics.entity.Grid;
import tech.pod.game.generics.entity.GridObject;
import tech.pod.game.generics.entity.Position;
import tech.pod.game.generics.ui.graphics.Color;
import tech.pod.game.generics.ui.graphics.GameImage;

public interface GameController<P extends Position, C extends Color>
{
    boolean ended();
    void end();
    GameController<P, C> pushActions(Action<P, C> action);
    GameController<P, C> readActions();
    GameController<P, C> executeActions();
    GameController<P, C> computeCollisions();
    GameController<P, C> updateStates();
    Grid<P> getGrid();
    GridObject<P> getGridObject(P position);
    GameController<P, C> addToGrid(GridObject<P> gridObject);
    void updateUI();
    void setUiUpdateCommands(List<BiConsumer<GridObject<P>, GameImage<C>>> uiUpdateCommands);
}
