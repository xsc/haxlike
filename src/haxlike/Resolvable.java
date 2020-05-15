package haxlike;

import java.util.List;

/**
 * Class representing an unresolved value.
 */
public interface Resolvable<T, R extends Resolvable<T, R>>
    extends Node.WithoutValue<T> {
    List<T> resolveAll(List<R> batch);

    @Override
    default List<Resolvable<?, ?>> allResolvables() {
        return List.of(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <V, I extends Resolvable<V, I>> Node<T> injectValue(
        I resolvable,
        V value
    ) {
        if (this.equals(resolvable)) {
            return Nodes.value((T) value);
        }
        return this;
    }
}
