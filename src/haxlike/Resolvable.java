package haxlike;

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
    default <V> Node<T> injectValues(
        Results<? extends Resolvable<V>, V> results
    ) {
        return results
            .get(this)
            .map(value -> (T) value)
            .<Node<T>>map(ValueNode::new)
            .orSome(() -> this);
    }
}
