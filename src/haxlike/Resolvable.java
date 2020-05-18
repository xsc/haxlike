package haxlike;

import fj.data.HashMap;
import fj.data.List;
import haxlike.nodes.ValueNode;

/**
 * Class representing an unresolved value.
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
}
