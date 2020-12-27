package tech.pod.game.generics.controller.td;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import tech.pod.game.generics.controller.core.Action;
import tech.pod.game.generics.controller.core.GameController;
import tech.pod.game.generics.entity.td.TDGrid;

public class TDGridController extends SubmissionPublisher<TDGrid> implements GameController<TDGrid>
{
    private boolean end = false;
    private boolean userEnd = false;
    private TDGrid grid;

    private final Object lock = new Object();
    private final Queue<Action<TDGrid>> actions = new LinkedList<>();
    private final Queue<Consumer<GameController<TDGrid>>> stateUpdaters = new LinkedList<>();

    public TDGridController()
    {
        super(Executors.newSingleThreadExecutor(), 1);
    }

    public TDGridController setGrid(TDGrid grid)
    {
        this.grid = Objects.requireNonNull(grid, "TDGridController: null grid");
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
        super.close();
        this.end();
        this.userEnd = true;
    }

    @Override
    public boolean userEnded()
    {
        return this.userEnd;
    }

    @Override
    public TDGrid getGrid()
    {
        return this.grid;
    }

    @Override
    public TDGridController addSubscriber(Flow.Subscriber<TDGrid> subscriber)
    {
        super.subscribe(subscriber);
        return this;
    }

    @Override
    public GameController<TDGrid> submitGrid()
    {
        if (!this.isClosed()) {
            TDGrid gridCopy;
            synchronized (this.lock) {
                gridCopy = this.grid.spawn();
            }
            this.submit(gridCopy);
        }
        return this;
    }

    @Override
    public TDGridController addUpdateEvent(Consumer<GameController<TDGrid>> stateUpdater)
    {
        Objects.requireNonNull(stateUpdater, "TDGridController: null state updater");
        this.stateUpdaters.add(stateUpdater);
        return this;
    }

    @Override
    public TDGridController updateStates()
    {
        while (!this.stateUpdaters.isEmpty()) {
            this.stateUpdaters.poll().accept(this);
        }
        return this;
    }

    @Override
    public TDGridController addAction(Action<TDGrid> action)
    {
        this.actions.add(action);
        return this;
    }

    @Override
    public TDGridController executeActions()
    {
        while (!this.actions.isEmpty()) {
            this.actions.poll().apply(this.grid);
        }
        return this;
    }

    @Override
    public synchronized TDGridController executeAction(Action<TDGrid> action)
    {
        synchronized (this.lock) {
            Objects.requireNonNull(action, "TDGridController: null action").apply(this.grid);
        }
        return this;
    }
}
