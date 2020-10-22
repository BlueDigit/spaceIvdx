package tech.pod.game.generics.entity;

import tech.pod.game.generics.controller.GameEvent;

public interface GridObject<P extends Position>
{
    void setPosition(P position);
    P getPosition();
    Era<P> getEra();
    boolean isInCollision(GridObject<P> other);
    boolean isCovering(GridObject<P> other);
    GridObject<P> copy();
    GridObject<P> takeEvent(GameEvent event);
}