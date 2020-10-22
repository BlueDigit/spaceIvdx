package tech.pod.game.generics.entity;


import tech.pod.game.generics.controller.GameEvent;


public class TwoDimensionalGameObject implements GridObject<TwoDimensionPosition>
{
    private TwoDimensionPosition position;
    private final Era<TwoDimensionPosition> era;

    public TwoDimensionalGameObject(TwoDimensionPosition position, Era<TwoDimensionPosition> era)
    {
        this.position = position;
        this.era = era;
    }

    @Override
    public void setPosition(TwoDimensionPosition position)
    {
        this.position = position;
    }

    @Override
    public TwoDimensionPosition getPosition()
    {
        return this.position;
    }

    @Override
    public Era<TwoDimensionPosition> getEra()
    {
        return this.era;
    }

    @Override
    public boolean isInCollision(GridObject<TwoDimensionPosition> other)
    {
        return false;
    }

    @Override
    public boolean isCovering(GridObject<TwoDimensionPosition> other)
    {
        return false;
    }

    @Override
    public GridObject<TwoDimensionPosition> copy()
    {
        return null;
    }

    @Override
    public GridObject<TwoDimensionPosition> takeEvent(GameEvent event)
    {
        return null;
    }
}

