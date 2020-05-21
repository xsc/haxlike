package haxlike;

import fj.*;
import fj.F;
import fj.data.HashMap;
import fj.data.List;
import haxlike.nodes.FlatMapNode;
import haxlike.nodes.MapNode;

/**
 * Representation of a, potentially nested, node eventually containing a value.
 * @param <T> the class of the value contained in the node.
 */
@SuppressWarnings("squid:S1452")
public interface Node<T> {
    /**
     * Check if this node is fully resolved.
     * @return true if fully resolved.
     */
    boolean isResolved();

    /**
     * Retrieve the value contained in this node. This needs to be guarded by
     * {@link Node#isResolved()}.
     * @return the value contained in the node.
     */
    T getValue();

    /**
     * Retrieve a list of all resolvables (including nested ones) from the node.
     * @return the list of resolvables.
     */
    List<Resolvable<?>> getResolvables();

    /**
     * Given a map of resolvables and their results, inject them into the node,
     * resulting in a partially or fully resolved node with the same value type.
     * @param <V> generic class representing resolvable results.
     * @param results map of resolvables and their results.
     * @return a partially or fully resolved node with the same value type.
     */
    <V> Node<T> injectValues(HashMap<Resolvable<V>, V> results);

    // --- Mappers
    /**
     * Apply the given function to the content of the node.
     * @param <R> return type of the function.
     * @param f the function to apply.
     * @return a new node containing the result of applying the function.
     */
    default <R> Node<R> map(F<T, R> f) {
        return new MapNode<>(this, f);
    }

    /**
     * Apply the given function to the content of the node, resulting in a new node.
     * @param <R> the class of the new node's value.
     * @param f the function to apply.
     * @return a new node representing the result of applying the function.
     */
    default <R> Node<R> flatMap(F<T, Node<R>> f) {
        return new FlatMapNode<>(this, f);
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
    default <A, R> Node<R> map(F2<T, A, R> f, Node<A> other) {
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
    default <A, R> Node<R> flatMap(F2<T, A, Node<R>> f, Node<A> other) {
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
    default <A, B, R> Node<R> map(F3<T, A, B, R> f, Node<A> a, Node<B> b) {
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
        F3<T, A, B, Node<R>> f,
        Node<A> a,
        Node<B> b
    ) {
        return Nodes.tuple(this, a, b).flatMap(f);
    }
}
