package haxlike;

import fj.data.List;

public final class Resolver {

    @FunctionalInterface
    public static interface Batched<E, V, R extends Resolvable<V>> {
        List<V> resolveAll(E environment, List<R> resolvables);
    }

    @FunctionalInterface
    public static interface Single<E, V, R extends Resolvable<V>> {
        V resolve(E env, R resolvable);
    }

    private Resolver() {}
}
