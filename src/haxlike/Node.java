package haxlike;

import fj.F;
import fj.data.List;
import haxlike.nodes.*;

@SuppressWarnings("squid:S1452")
public interface Node<T> {
    boolean isResolved();
    T getValue();
    List<Resolvable<?>> getResolvables();
    <V> Node<T> injectValue(Resolvable<V> resolvable, V value);

    // --- Functions
    default <R> Node<R> map(F<T, R> f) {
        return new MapNode<>(this, f);
    }

    default <R> Node<R> flatMap(F<T, Node<R>> f) {
        return new FlatMapNode<>(this, f);
    }

    default <B> PairNode<T, B> with(Node<B> other) {
        return new PairNode<>(this, other);
    }
}
