package tech.pod.game.generics.controller.td;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import tech.pod.game.generics.controller.core.Action;
import tech.pod.game.generics.controller.core.GameController;
import tech.pod.game.generics.entity.core.Grid;
import tech.pod.game.generics.entity.td.TDMaterial;
import tech.pod.game.generics.entity.td.TDPosition;
import tech.pod.game.generics.ui.graphics.Color;
import tech.pod.game.generics.ui.graphics.GameImage;
import tech.pod.game.generics.ui.graphics.GameScreen;

public class TDGridController<C extends Color> implements GameController<TDPosition, C, TDMaterial>
{
    private Queue<Action<GameController<TDPosition, C, TDMaterial>>> actions = new LinkedList<>();
    private GameScreen<C> screen;
    private Function<TDGridController<C>, GameImage<C>> screenConverter;
    private Grid<TDPosition, TDMaterial> grid;

    public TDGridController<C> setGrid(Grid<TDPosition, TDMaterial> grid)
    {
        this.grid = Objects.requireNonNull(grid, "TDGridController: null grid");
        return this;
    }

    public TDGridController<C> setScreen(GameScreen<C> screen, Function<TDGridController<C>, GameImage<C>> screenConverter)
    {
        this.screen = Objects.requireNonNull(screen, "TDGridController: null screen");
        this.screenConverter = screenConverter;
        return this;
    }

    @Override
    public void end()
    {
    }

    @Override
    public boolean ended()
    {
        return false;
    }

    @Override
    public Grid<TDPosition, TDMaterial> getGrid()
    {
        return this.grid;
    }

    @Override
    public GameScreen<C> getScreen()
    {
        return this.screen;
    }

    @Override
    public TDGridController<C> updateUI()
    {
        final var image = this.screenConverter.apply(this);
        this.screen.draw(image);
        return this;
    }

    @Override
    public TDGridController<C> updateStates()
    {
        return null;
    }

    @Override
    public TDGridController<C> executeActions()
    {
        while (!this.actions.isEmpty()) {
            this.actions.poll().apply(this);
        }
        return this;
    }

    @Override
    public TDGridController<C> addAction(Action<GameController<TDPosition, C, TDMaterial>> action)
    {
        this.actions.add(action);
        return this;
    }

    @Override
    public TDGridController<C> executeAction(Action<GameController<TDPosition, C, TDMaterial>> action)
    {
        return null;
    }

    @Override
    public TDGridController<C> addUIUpdateAction(Action<GameController<TDPosition, C, TDMaterial>> action)
    {
        return null;
    }
}
