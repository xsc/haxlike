package haxlike;

import fj.*;
import fj.F;
import fj.data.HashMap;
import fj.data.List;
import haxlike.nodes.*;

@SuppressWarnings("squid:S1452")
public interface Node<T> {
    boolean isResolved();
    T getValue();
    List<Resolvable<?>> getResolvables();
    <V> Node<T> injectValues(HashMap<Resolvable<V>, V> results);

    // --- Functions
    default <R> Node<R> map(F<T, R> f) {
        return new MapNode<>(this, f);
    }

    default <R> Node<R> flatMap(F<T, Node<R>> f) {
        return new FlatMapNode<>(this, f);
    }

    default <A, R> Node<R> map(F2<T, A, R> f, Node<A> other) {
        return Nodes.map(f, this, other);
    }

    default <A, R> Node<R> flatMap(F2<T, A, Node<R>> f, Node<A> other) {
        return Nodes.flatMap(f, this, other);
    }

    default <A, B, R> Node<R> map(F3<T, A, B, R> f, Node<A> a, Node<B> b) {
        return Nodes.map(f, this, a, b);
    }

    default <A, B, R> Node<R> flatMap(
        F3<T, A, B, Node<R>> f,
        Node<A> a,
        Node<B> b
    ) {
        return Nodes.flatMap(f, this, a, b);
    }
}
