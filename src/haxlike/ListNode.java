package haxlike;

import fj.F;
import fj.F2;
import fj.F3;
import fj.data.List;
import haxlike.nodes.CollectionNode;
import haxlike.nodes.NodeDecorator;

public interface ListNode<T> extends Node<List<T>> {
    /**
     * Similar to {@link ListNode#flatMapEach} but will pass {@link Node} values
     * to the function. This allows for a more fluid style when building up resolvable
     * trees.
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results.
     */
    default <R> ListNode<R> traverse(F<Node<T>, Node<R>> f) {
        return this.flatMapEach(value -> f.f(Nodes.value(value)));
    }

    /**
     * Similar to {@link ListNode#mapEach} but will pass {@link Node} values
     * to the function. This allows for a more fluid style when building up resolvable
     * trees.
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results.
     */
    default <R> ListNode<R> collect(F<Node<T>, R> f) {
        return this.mapEach(value -> f.f(Nodes.value(value)));
    }

    /**
     * Apply the given function to each element of the list, resulting in a new
     * collection of new nodes to resolve.
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results
     */
    default <R> ListNode<R> flatMapEach(F<T, Node<R>> f) {
        return new Decorator<>(
            this.flatMap(xs -> new CollectionNode<>(xs.map(f)))
        );
    }

    /**
     * Apply the given function to each element of the list
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results
     */
    default <R> ListNode<R> mapEach(F<T, R> f) {
        return new Decorator<>(this.map(xs -> xs.map(f)));
    }

    /**
     * Run the given attachment function for each element of this list to create
     * a value to attach. Afterwards call the given 'attach' function on the
     * original value and the value to attach.
     * @param <V> result value class
     * @param <R> attachment value class
     * @param attach function to combine the original value with the attachment
     * @param attachment function to generate the attachment
     * @return node containing a list where every element is the combination of the original element and an attachment.
     */
    default <V, R> ListNode<V> attachEach(
        F2<T, R, V> attach,
        F<T, Node<R>> attachment
    ) {
        return this.flatMapEach(
                value -> Nodes.value(value).attach(attach, attachment)
            );
    }

    /**
     * Run the given attachment functions for each element of this list to create
     * values to attach. Afterwards call the given 'attach' function on the
     * original value and the values to attach.
     * @param <V> result value class
     * @param <A> first attachment value class
     * @param <B> second attachment value class
     * @param attach function to combine the original value with the attachment
     * @param firstAttachment function to generate the first attachment
     * @param secondAttachment function to generate the second attachment
     * @return node containing a list where every element is the combination of
     * the original element and an attachment.
     */
    default <V, A, B> ListNode<V> attachEach(
        F3<T, A, B, V> attach,
        F<T, Node<A>> firstAttachment,
        F<T, Node<B>> secondAttachment
    ) {
        return this.flatMapEach(
                value ->
                    Nodes
                        .value(value)
                        .attach(attach, firstAttachment, secondAttachment)
            );
    }

    default <R> Node<R> foldLeft(F2<R, T, R> f, R acc) {
        return this.map(xs -> xs.foldLeft(f, acc));
    }

    default <R> Node<R> foldRight(F2<T, R, R> f, R acc) {
        return this.map(xs -> xs.foldRight(f, acc));
    }

    /**
     * Simple decorator to add list traversal functionality to any list-containing
     * {@link Node}.
     * @param <T> element class.
     */
    static class Decorator<T>
        extends NodeDecorator<List<T>>
        implements ListNode<T> {

        Decorator(Node<List<T>> inner) {
            super(inner);
        }
    }
}
