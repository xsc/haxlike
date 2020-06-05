package haxlike;

import fj.data.List;
import haxlike.projections.IdentityProjection;
import haxlike.projections.InspectProjection;
import haxlike.projections.ListProjection;
import haxlike.projections.SelectProjection;

/**
 * Interface for tree projections. These can perform arbitrary transformations
 * on a node, as long as they don't change the overall type.
 * @param <T> node value class
 */
public interface Projection<T> {
    /**
     * Apply projection to the node.
     * @param node node to project
     * @return projected node
     */
    Node<T> project(Node<T> node);

    /**
     * Create an identity projection for the given class. Calling this projection
     * on a node will not alter the node.
     * @param <T> class this projection applies to
     * @param cls class reference this projection applies to
     * @return a new identity projection
     */
    @SuppressWarnings("squid:S1172")
    static <T> Projection<T> projection(Class<T> cls) {
        return new IdentityProjection<>();
    }

    /**
     * Add a relation to this projection.
     * @param <R> relation value class
     * @param relation relation to add to the projection
     * @param projection projection to apply to the relation value
     * @return a projection that will attach the given relation to the projected node
     */
    default <R> Projection<T> select(
        Relation<T, R> relation,
        Projection<R> projection
    ) {
        return new SelectProjection<>(this, relation, projection);
    }

    /**
     * Add a relation to this projection.
     * @param <R> relation value class
     * @param relation relation to add to the projection
     * @return a projection that will attach the given relation to the projected node
     */
    @SuppressWarnings("varargs")
    default <R> Projection<T> select(Relation<T, R> relation) {
        return select(relation, new IdentityProjection<>());
    }

    /**
     * Convert this projection to a list projection that will be applied to
     * every element of a list node.
     * @return a list projection
     */
    default Projection<List<T>> list() {
        return new ListProjection<>(this);
    }

    /**
     * Logs the currently projected value.
     * @return a projection that will cause the projected node's value to be logged
     */
    default Projection<T> inspect() {
        return new InspectProjection<>(this);
    }
}
