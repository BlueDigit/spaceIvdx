package tech.pod.game.generics.entity.td;

import tech.pod.game.generics.entity.core.Vector;

public class TDVector implements Vector<TDPosition, TDMaterial>
{
    public final int x;
    public final int y;

    protected TDVector(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public TDMaterial apply(TDMaterial material)
    {
        var upperLeft = TDPosition.of(material.upperLeft.x + this.x,
                                      material.upperLeft.y + this.y);
        var lowerRight = TDPosition.of(material.lowerRight.x + this.x,
                                       material.lowerRight.y + this.y);
        return material.setUpperLeft(upperLeft).setLowerRight(lowerRight);
    }

    public static TDVector of(int x, int y)
    {
        return new TDVector(x, y);
    }
}
