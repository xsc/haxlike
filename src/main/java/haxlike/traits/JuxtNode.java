package haxlike.traits;

import fj.F;
import haxlike.Node;
import haxlike.Nodes;
import haxlike.PlainNode;
import haxlike.nodes.tuples.Tuple2;
import haxlike.nodes.tuples.Tuple3;

/**
 * Trait for parallel application of functions to a node's value.
 * @param <T> node value class
 */
public interface JuxtNode<T> extends PlainNode<T> {
    /**
     * Create a tuple with two elements, each representing the application
     * of one of the given functions on the current node's value.
     * @param <A> result value class
     * @param <B> result value class
     * @param fa first function to apply
     * @param fb second function to apply
     * @return a tuple of two elements
     */
    default <A, B> Tuple2<A, B> juxt(
        F<T, PlainNode<A>> fa,
        F<T, PlainNode<B>> fb
    ) {
        return Nodes.tuple(this.flatMap(fa), this.flatMap(fb));
    }

    /**
     * Create a tuple with three elements, each representing the application
     * of one of the given functions on the current node's value.
     * @param <A> result value class
     * @param <B> result value class
     * @param <C> result value class
     * @param fa first function to apply
     * @param fb second function to apply
     * @param fc third function to apply
     * @return a tuple of three elements
     */
    default <A, B, C> Tuple3<A, B, C> juxt(
        F<T, Node<A>> fa,
        F<T, Node<B>> fb,
        F<T, Node<C>> fc
    ) {
        return Nodes.tuple(
            this.flatMap(fa),
            this.flatMap(fb),
            this.flatMap(fc)
        );
    }

    /**
     * Create a tuple with two elements, the first one being the current node,
     * the other representing the application of the given function on the
     * current node's value.
     * @param <A> result value class
     * @param fa first function to apply
     * @return a tuple of two elements
     */
    default <A> Tuple2<T, A> juxtSelf(F<T, Node<A>> f) {
        return Nodes.tuple(this, this.flatMap(f));
    }

    /**
     * Create a tuple with three elements, the first one being the current node,
     * the others each representing the application of one of the given functions
     * on the current node's value.
     * @param <A> result value class
     * @param <B> result value class
     * @param fa first function to apply
     * @param fb second function to apply
     * @return a tuple of three elements
     */
    default <A, B> Tuple3<T, A, B> juxtSelf(
        F<T, Node<A>> fa,
        F<T, Node<B>> fb
    ) {
        return Nodes.tuple(this, this.flatMap(fa), this.flatMap(fb));
    }
}
