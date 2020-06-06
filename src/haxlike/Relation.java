package haxlike;

import fj.F;
import fj.F2;
import haxlike.impl.RelationImpl;

/**
 * Describes a generic relation to a node, i.e. something that can be fetched
 * and then injected into a container value.
 *
 * @param <T> class of the container value
 * @param <V> class of the relation
 */
public interface Relation<T, V, R extends Resolvable<V>> {
    /**
     * Node representing the relation value.
     * @return the node whose resolution will result in the relation value
     */
    F<T, R> getNodeFunction();

    /**
     * Attach the relation to the value.
     * @param value container value
     * @param relation relation value
     * @return container value with the relation injected
     */
    F2<T, V, T> getAttachFunction();

    /**
     * Apply the given parameter to the relation
     * @param <V> parameter value class
     * @param param parameter to apply
     * @param value parameter value
     * @return relation with the given parameter applied
     */
    default <P> Relation<T, V, R> with(Parameter<? super R, P> param, P value) {
        return new RelationImpl<>(
            this.getAttachFunction(),
            v -> param.attach(this.getNodeFunction().f(v), value)
        );
    }

    /**
     * Create a new relation for this node. This should only be called from inside
     * the resolvable implementation.
     * @param <R> relation value class
     * @param attachFunction relation attachment function
     * @param nodeFunction node creation function
     * @return the desired relation
     */
    static <T, V, N extends Resolvable<V>> Relation<T, V, N> declare(
        F2<T, V, T> attachFunction,
        F<T, N> nodeFunction
    ) {
        return new RelationImpl<>(attachFunction, nodeFunction);
    }
}
