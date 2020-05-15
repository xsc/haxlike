package haxlike;

import haxlike.nodes.FlatMapNode;
import haxlike.nodes.MapNode;
import haxlike.nodes.WithNode;
import java.util.List;
import java.util.function.Function;

public interface Node<T> {
    // --- Resolvables
    @SuppressWarnings("squid:S1452")
    default List<Resolvable<?, ?>> allResolvables() {
        return List.of();
    }

    default <V, R extends Resolvable<V, R>> Node<T> injectValue(
        R resolvable,
        V value
    ) {
        return this;
    }

    // --- Contents
    boolean hasValue();

    default T getValue() {
        throw new IllegalStateException("This node does not have a value.");
    }

    // --- Functions
    default <R> Node<R> map(Function<T, R> f) {
        return new MapNode<>(this, f);
    }

    default <R> Node<R> flatMap(Function<T, Node<R>> f) {
        return new FlatMapNode<>(this, f);
    }

    default <B> WithNode<T, B> with(Node<B> other) {
        return new WithNode<>(this, other);
    }

    // --- Traits
    interface WithValue<T> extends Node<T> {
        @Override
        default boolean hasValue() {
            return true;
        }
    }

    interface WithoutValue<T> extends Node<T> {
        @Override
        default boolean hasValue() {
            return false;
        }
    }
}
