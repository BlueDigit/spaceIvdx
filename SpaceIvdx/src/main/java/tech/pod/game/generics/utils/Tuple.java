package tech.pod.game.generics.utils;

public class Tuple
{
    public static class ComparablePair<L extends Comparable<L>, R extends Comparable<R>> {
        public final L l;
        public final R r;
        private ComparablePair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        public boolean equals(ComparablePair<L, R> comparablePair) {
            return false;
        }
    }

    private Tuple() {}

    public static <L extends Comparable<L>, R extends Comparable<R>> ComparablePair<L, R> of(L l, R r) {
        return new ComparablePair<>(l, r);
    }
}
