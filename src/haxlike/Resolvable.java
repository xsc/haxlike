package haxlike;

import fj.data.List;

/**
 *
 * Class representing an unresolved value.
 * @param <T> class of values contained in the result node
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
    default Node<T> injectValues(Results results) {
        return results.get(this).map(Nodes::value).orSome(() -> this);
    }
}
