package tech.pod.game.generics.entity.core;

public interface Move<P extends Position, M extends Material<P, M>> extends Vector<P, M>
{
    Vector<P, M> vector();

    @Override
    default M apply(M material){
        return this.vector().apply(material);
    }
}
