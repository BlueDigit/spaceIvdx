package tech.pod.game.generics.entity.td;

import java.util.Objects;
import tech.pod.game.generics.entity.core.Position;

public class TDPosition implements Position, Comparable<TDPosition>
{
    final int x;
    final int y;

    private TDPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static TDPosition of(int x, int y) {
        return new TDPosition(x, y);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TDPosition)) {
            return false;
        }
        var other = (TDPosition)object;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return ("x:" + this.x + "y:" + this.y).hashCode();
    }

    @Override
    public int compareTo(TDPosition o) {
        Objects.requireNonNull(o, "TDPosition: Null other position");
        if (this.y < o.y) {
            return -1;
        } else if (this.y == o.y) {
            return Integer.compare(this.x, o.x);
        }
        return 1;
    }
}
