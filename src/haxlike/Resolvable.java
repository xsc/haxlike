package haxlike;

import fj.data.List;

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
    default <V> Node<T> injectValue(Resolvable<V> resolvable, V value) {
        if (this.equals(resolvable)) {
            return Nodes.value((T) value);
        }
        return this;
    }
}
