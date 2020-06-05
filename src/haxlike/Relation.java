package haxlike;

import fj.F;
import fj.F2;
import haxlike.impl.RelationImpl;

/**
 * Describes a generic relation to a node, i.e. something that can be fetched
 * and then injected into a container value.
 *
 * @param <T> class of the container value
 * @param <R> class of the relation
 */
public interface Relation<T, R> {
    /**
     * Node representing the relation value.
     * @return the node whose resolution will result in the relation value
     */
    F<T, Node<R>> getNodeFunction();

    /**
     * Attach the relation to the value.
     * @param value container value
     * @param relation relation value
     * @return container value with the relation injected
     */
    F2<T, R, T> getAttachFunction();

    /**
     * Create a new relation for this node. This should only be called from inside
     * the resolvable implementation.
     * @param <R> relation value class
     * @param attachFunction relation attachment function
     * @param nodeFunction node creation function
     * @return the desired relation
     */
    static <T, R> Relation<T, R> declare(
        F2<T, R, T> attachFunction,
        F<T, Node<R>> nodeFunction
    ) {
        return new RelationImpl<>(attachFunction, nodeFunction);
    }
}
