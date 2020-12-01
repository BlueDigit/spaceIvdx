package tech.pod.game.generics.controller.td;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
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
    private boolean end = false;
    private boolean userEnd = false;
    private Grid<TDPosition, TDMaterial> grid;
    private GameScreen<C> screen;
    private Function<TDGridController<C>, GameImage<C>> screenConverter;

    private final Queue<Action<GameController<TDPosition, C, TDMaterial>>> actions = new LinkedList<>();
    private final Queue<Consumer<GameController<TDPosition, C, TDMaterial>>> stateUpdaters = new LinkedList<>();

    public TDGridController<C> setGrid(Grid<TDPosition, TDMaterial> grid) {
        this.grid = Objects.requireNonNull(grid, "TDGridController: null grid");
        return this;
    }

    public TDGridController<C> setScreen(
            GameScreen<C> screen,
            Function<TDGridController<C>, GameImage<C>> screenConverter)
    {
        this.screen = Objects.requireNonNull(screen, "TDGridController: null screen");
        this.screenConverter = screenConverter;
        return this;
    }

    @Override
    public void end()
    {
        this.end = true;
    }

    @Override
    public void restart()
    {
        this.end = false;
    }

    @Override
    public boolean ended()
    {
        return this.end;
    }

    @Override
    public void userEnd()
    {
        this.end = true;
        this.userEnd = true;
    }

    @Override
    public boolean userEnded()
    {
        return this.userEnd;
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
    public synchronized TDGridController<C> updateUI()
    {
        final var image = this.screenConverter.apply(this);
        this.screen.draw(image);
        return this;
    }

    @Override
    public TDGridController<C> updateStates()
    {
        while (!this.stateUpdaters.isEmpty()) {
            this.stateUpdaters.poll().accept(this);
        }
        return this;
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
    public synchronized TDGridController<C> executeAction(Action<GameController<TDPosition, C, TDMaterial>> action)
    {
        Objects.requireNonNull(action, "TDGridController: null action").apply(this);
        return this;
    }

    @Override
    public TDGridController<C> addStateUpdater(Consumer<GameController<TDPosition, C, TDMaterial>> stateUpdater)
    {
        this.stateUpdaters.add(stateUpdater);
        return this;
    }

    @Override
    public TDGridController<C> addUIUpdateAction(Action<GameController<TDPosition, C, TDMaterial>> action)
    {
        throw new UnsupportedOperationException("Add update ui action is not supported");
    }
}
