package tech.pod.game.generics.entity;

import java.util.List;

public interface Era<P extends Position>
{
    List<P> getPositions();
    int era();
}
