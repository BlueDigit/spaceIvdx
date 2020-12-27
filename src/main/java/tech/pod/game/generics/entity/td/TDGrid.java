package tech.pod.game.generics.entity.td;

import tech.pod.game.generics.entity.core.Grid;

public interface TDGrid extends Grid<TDPosition, TDMaterial>
{
    @Override
    TDGrid spawn();

    int getWidth();

    int getCellWidth();
}
