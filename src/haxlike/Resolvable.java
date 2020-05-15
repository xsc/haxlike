package haxlike;

import java.util.List;

/**
 * Class representing an unresolved value.
 */
public interface Resolvable<T> extends Node<T> {
    @Override
    default boolean isResolved() {
        return false;
    }

    @Override
    default List<Resolvable<?>> allResolvables() {
        return List.of(this);
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
