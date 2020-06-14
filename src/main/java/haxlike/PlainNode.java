package haxlike;

import fj.F;
import fj.data.List;
import haxlike.resolvers.Results;

/**
 * Basic node interface. Do not use this directly, but rely on {@link Node}.
 * @param <T> node value class
 */
@SuppressWarnings("squid:S1452")
public interface PlainNode<T> {
    /**
     * Check if this node is fully resolved.
     * @return true if fully resolved.
     */
    boolean isResolved();

    /**
     * Retrieve the value contained in this node. This needs to be guarded by
     * {@link PlainNode#isResolved()}.
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
    PlainNode<T> injectValues(Results<Resolvable<?>, ?> results);

    /**
     * Apply the given function to the content of the node.
     * @param <R> return type of the function.
     * @param f the function to apply.
     * @return a new node containing the result of applying the function.
     */
    <R> Node<R> map(F<T, R> f);

    /**
     * Apply the given function to the content of the node, resulting in a new node.
     * @param <R> the class of the new node's value.
     * @param f the function to apply.
     * @return a new node representing the result of applying the function.
     */
    <R> Node<R> flatMap(F<T, ? extends PlainNode<R>> f);
}
