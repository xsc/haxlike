package haxlike;

import haxlike.nodes.*;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("squid:S1452")
public interface Node<T> {
    boolean isResolved();
    T getValue();
    List<Resolvable<?>> allResolvables();
    <V> Node<T> injectValue(Resolvable<V> resolvable, V value);

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
}
