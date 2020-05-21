package haxlike;

import fj.data.HashMap;
import fj.data.List;
import haxlike.nodes.ValueNode;

/**
 *
 * Class representing an unresolved value.
 * @param <T> class of resolved values
 */
public interface Resolvable<T> extends Node<T> {
    @Override
    default boolean isResolved() {
        return false;
    }

    @Override
    default T getValue() {
        throw new UnsupportedOperationException(
            "Cannot call 'getValue' on Resolvable."
        );
    }

    @Override
    default List<Resolvable<?>> getResolvables() {
        return List.single(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <V> Node<T> injectValues(HashMap<Resolvable<V>, V> results) {
        return results
            .get((Resolvable<V>) this)
            .map(value -> (T) value)
            .<Node<T>>map(ValueNode::new)
            .orSome(() -> this);
    }

    /**
     * Utility interface to declare a resolvable with inlined resolution logic (single).
     * @param <E> the environment class the resolver supports
     * @param <T> the class of resolved values
     * @param <R> this needs to be set to the implementing resolvable class
     */
    public interface Single<E, T, R extends Single<E, T, R>>
        extends Resolvable<T>, Resolver.Single<E, T, R> {}

    /**
     * Utility interface to declare a resolvable with inlined resolution logic (batched).
     * @param <E> the environment class the resolver supports
     * @param <T> the class of resolved values
     * @param <R> this needs to be set to the implementing resolvable class
     */
    public interface Batched<E, T, R extends Batched<E, T, R>>
        extends Resolvable<T>, Resolver.Batched<E, T, R> {}
}
