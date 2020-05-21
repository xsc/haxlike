package haxlike;

import fj.data.List;

public final class Resolver {

    /**
     * A batched resolver is a function that will use a supplied environment
     * to return a list of results based on a list of values-to-resolve.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface Batched<E, V, R extends Resolvable<V>> {
        List<V> resolveAll(E environment, List<R> resolvables);
    }

    /**
     * A single resolver is a function that will use a supplied environment
     * to return a single result based on a value-to-resolve.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface Single<E, V, R extends Resolvable<V>> {
        V resolve(E env, R resolvable);
    }

    private Resolver() {}
}
