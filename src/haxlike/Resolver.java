package haxlike;

import fj.data.List;

@FunctionalInterface
public interface Resolver<E, V, R extends Resolvable<V>> {
    List<V> resolveAll(E environment, List<R> resolvables);
}
