package haxlike.relations;

import fj.F;
import fj.F2;
import haxlike.Node;
import haxlike.relations.impl.ParameterRelationImpl;
import haxlike.relations.impl.RelationImpl;

/**
 * Describes a generic relation to a node, i.e. something that can be fetched
 * and then injected into a container value.
 *
 * @param <T> class of the container value
 * @param <V> class of the relation
 */
public interface Relation<T, V> {
    /**
     * Node representing the relation value.
     * @return the node whose resolution will result in the relation value
     */
    F<T, Node<V>> getNodeFunction();

    /**
     * Attach the relation to the value.
     * @param value container value
     * @param relation relation value
     * @return container value with the relation injected
     */
    F2<T, V, T> getAttachFunction();

    /**
     * Create a new relation for this node. This should only be called from inside
     * the resolvable implementation.
     * @param <R> relation value class
     * @param attachFunction relation attachment function
     * @param nodeFunction node creation function
     * @return the desired relation
     */
    static <T, V> Relation<T, V> declare(
        F2<T, V, T> attachFunction,
        F<T, Node<V>> nodeFunction
    ) {
        return new RelationImpl<>(attachFunction, nodeFunction);
    }

    /**
     * Create a new relation for this node. This should only be called from inside
     * the resolvable implementation.
     * @param <R> relation value class
     * @param attachFunction relation attachment function
     * @param nodeFunction node creation function
     * @return the desired relation
     */
    static <T, P, V> ParameterRelation<T, P, V> declareWithParameters(
        F2<T, V, T> attachFunction,
        F<P, Node<V>> nodeFunction,
        F<T, P> parameterFunction
    ) {
        return new ParameterRelationImpl<>(
            attachFunction,
            nodeFunction,
            parameterFunction
        );
    }
}
