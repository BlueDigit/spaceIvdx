package tech.pod.game.generics.entity.td;

import java.util.function.Function;
import tech.pod.game.generics.entity.core.Vector;

public enum TDMoves
{
    UP(y -> tdMaterial ->
            tdMaterial
                    .setUpperLeft(TDPosition.of(tdMaterial.upperLeft.x, tdMaterial.upperLeft.y - y))
                    .setLowerRight(TDPosition.of(tdMaterial.lowerRight.x, tdMaterial.lowerRight.y - y))
    ),
    DOWN(y -> tdMaterial ->
            tdMaterial
                    .setUpperLeft(TDPosition.of(tdMaterial.upperLeft.x, tdMaterial.upperLeft.y + y))
                    .setLowerRight(TDPosition.of(tdMaterial.lowerRight.x, tdMaterial.lowerRight.y + y))
    ),
    LEFT(x -> tdMaterial ->
            tdMaterial
                    .setUpperLeft(TDPosition.of(tdMaterial.upperLeft.x - x, tdMaterial.upperLeft.y))
                    .setLowerRight(TDPosition.of(tdMaterial.lowerRight.x - x, tdMaterial.lowerRight.y))
    ),
    RIGHT(x -> tdMaterial ->
            tdMaterial
                    .setUpperLeft(TDPosition.of(tdMaterial.upperLeft.x + x, tdMaterial.upperLeft.y))
                    .setLowerRight(TDPosition.of(tdMaterial.lowerRight.x + x, tdMaterial.lowerRight.y))
    );

    private final Function<Integer, Vector<TDPosition, TDMaterial>> vectorComputer;

    TDMoves(Function<Integer, Vector<TDPosition, TDMaterial>> vectorComputer) {
        this.vectorComputer = vectorComputer;
    }

    public Vector<TDPosition, TDMaterial> computeVector(int x) {
        return this.vectorComputer.apply(x);
    }

    public Vector<TDPosition, TDMaterial> composeLeft(int x, Vector<TDPosition, TDMaterial> vector) {
        return material -> this.vectorComputer.apply(x).apply(vector.apply(material));
    }

    public Vector<TDPosition, TDMaterial> composeRight(int x, Vector<TDPosition, TDMaterial> vector) {
        return material -> vector.apply(this.vectorComputer.apply(x).apply(material));
    }
}
