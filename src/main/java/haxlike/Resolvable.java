package haxlike;

import fj.data.List;
import haxlike.resolvers.Results;

/**
 * Class representing an unresolved value.
 * @param <T> class of values contained in the result node
 */
public interface Resolvable<T> extends Node<T> {
    /**
     * Return resolvable key, a value that is used to group resolvables that can
     * be resolved by the same resolver.
     * @return resolvable key string.
     */
    default String getResolvableKey() {
        return this.getClass().getName();
    }

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
    default Node<T> injectValues(Results<Resolvable<?>, ?> results) {
        return results
            .get(this)
            .map(v -> (T) v)
            .map(Nodes::value)
            .orSome(() -> this);
    }

    /**
     * Convenience trait, enriching a {@link Resolvable} returning a list with
     * {@link ListNode} functionality, including {@link ListNode#mapEach},
     * {@link ListNode#flatMapEach} and others.
     *
     * @param <T> list element class
     */
    public static interface ListResolvable<T>
        extends Resolvable<List<T>>, Node.ListNode<T> {}
}
