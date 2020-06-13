package haxlike.traits;

import fj.F;
import fj.F2;
import fj.F3;
import haxlike.Node;
import haxlike.Nodes;
import haxlike.PlainNode;
import haxlike.nodes.FlatMapNode;
import haxlike.nodes.MapNode;

/**
 * Trait adding <code>map</code> and <code>flatMap</code> methods for multilpe values.
 * @param <T> node value class
 */
public interface ComposeNode<T> extends PlainNode<T> {
    @Override
    default <R> Node<R> map(F<T, R> f) {
        return new MapNode<>(this, f);
    }

    @Override
    default <R> Node<R> flatMap(F<T, ? extends PlainNode<R>> f) {
        if (this.isResolved()) {
            return new FlatMapNode.Resolved<>(this, f);
        } else {
            return new FlatMapNode<>(this, f);
        }
    }

    /**
     * Like {@link Node#map(F)} but takes an additional argument.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node)}:
     * <pre>
     * Nodes.tuple(node, other).map(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node containing the result of applying the function.
     */
    default <A, R> Node<R> map(F2<T, A, R> f, PlainNode<A> other) {
        return Nodes.tuple(this, other).map(f);
    }

    /**
     * Like {@link Node#flatMap(F)} but takes an additional argument.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node)}:
     * <pre>
     * Nodes.tuple(node, other).flatMap(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node representing the result of applying the function.
     */
    default <A, R> Node<R> flatMap(F2<T, A, PlainNode<R>> f, Node<A> other) {
        return Nodes.tuple(this, other).flatMap(f);
    }

    /**
     * Like {@link Node#map(F)} but takes two additional arguments.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node, Node)}:
     * <pre>
     * Nodes.tuple(node, a, b).map(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <B> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node containing the result of applying the function.
     */
    default <A, B, R> PlainNode<R> map(
        F3<T, A, B, R> f,
        PlainNode<A> a,
        PlainNode<B> b
    ) {
        return Nodes.tuple(this, a, b).map(f);
    }

    /**
     * Like {@link Node#flatMap(F)} but takes two additional arguments.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node, Node)}:
     * <pre>
     * Nodes.tuple(node, a, b).flatMap(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <B> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node representing the result of applying the function.
     */
    default <A, B, R> Node<R> flatMap(
        F3<T, A, B, PlainNode<R>> f,
        Node<A> a,
        Node<B> b
    ) {
        return Nodes.tuple(this, a, b).flatMap(f);
    }
}
