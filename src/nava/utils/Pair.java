package nava.utils;

import java.io.Serializable;

public class Pair<L, R> implements Serializable {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
       long l = left.hashCode() * 2654435761L;
       return (int)l + (int)(l >>> 32) + right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft())
                && this.right.equals(pairo.getRight());
    }
}