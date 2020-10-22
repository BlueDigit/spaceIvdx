package tech.pod.game.generics.utils;

public class Tuple
{
    public static class Pair<L, R> {
        public final L l;
        public final R r;
        private Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }
    }

    private Tuple() {}

    public static <L, R> Pair<L, R> of(L l, R r) {
        return new Pair<>(l, r);
    }
}
