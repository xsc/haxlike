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
     * Add another projection to be applied.
     * @param projection next projection to be applied
     * @return a new projection that combines the current one and the given one
     */
    default Projection<T> and(AndProjection<T> projection) {
        return projection.wrap(this);
    }

    /**
     * Add a projection that represents a single relation on the target entity
     * @param <T> node value class
     * @param <R> relation value class
     * @param relation relation to add to the projection
     * @param projection projection to apply to the relation value
     * @return a projection that will attach the given relation to the projected node
     */
    default <V, R extends Resolvable<V>> Projection<T> andSelect(
        Relation<T, V, R> relation,
        Projection<V> projection
    ) {
        return this.and(select(relation, projection));
    }

    /**
     * Add a projection that represents a single relation on the target entity
     * @param <T> node value class
     * @param <R> relation value class
     * @param relation relation to add to the projection
     * @return a projection that will attach the given relation to the projected node
     */
    default <V, R extends Resolvable<V>> Projection<T> andSelect(
        Relation<T, V, R> relation
    ) {
        return this.and(select(relation));
    }

    /**
     * Add a projection that represents a list relation on the given entity,
     * applying the given sub-projection to every value
     * @param <T> node value class
     * @param <R> relation value class
     * @param <V> list element class
     * @param relation relation to add to the projection
     * @param projection projection to apply to each element of the relation's result
     * @return a projection that will attach the given relation to every element of the relation's result
     */
    default <V, R extends Resolvable<List<V>>> Projection<T> andSelectList(
        Relation<T, List<V>, R> relation,
        Projection<V> projection
    ) {
        return this.and(selectList(relation, projection));
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
     * Projection that can be chained with another.
     * @param <T> node value class
     */
    @FunctionalInterface
    public interface AndProjection<T> extends Projection<T> {
        Projection<T> wrap(Projection<T> projection);

        @Override
        default Node<T> project(Node<T> node) {
            return this.wrap(new IdentityProjection<>()).project(node);
        }
    }

    /**
     * Create a projection that represents a single relation on the target entity
     * @param <T> node value class
     * @param <R> relation value class
     * @param relation relation to add to the projection
     * @param projection projection to apply to the relation value
     * @return a projection that will attach the given relation to the projected node
     */
    static <T, V, R extends Resolvable<V>> AndProjection<T> select(
        Relation<T, V, R> relation,
        Projection<V> projection
    ) {
        return p -> new SelectProjection<>(p, relation, projection);
    }

    /**
     * Create a projection that represents a single relation on the target entity
     * @param <T> node value class
     * @param <R> relation value class
     * @param relation relation to add to the projection
     * @return a projection that will attach the given relation to the projected node
     */
    static <T, V, R extends Resolvable<V>> AndProjection<T> select(
        Relation<T, V, R> relation
    ) {
        return p ->
            new SelectProjection<>(p, relation, new IdentityProjection<>());
    }

    /**
     * Create a projection that represents a list relation on the given entity,
     * applying the given sub-projection to every value
     * @param <T> node value class
     * @param <R> relation value class
     * @param <V> list element class
     * @param relation relation to add to the projection
     * @param projection projection to apply to each element of the relation's result
     * @return a projection that will attach the given relation to every element of the relation's result
     */
    static <T, V, R extends Resolvable<List<V>>> AndProjection<T> selectList(
        Relation<T, List<V>, R> relation,
        Projection<V> projection
    ) {
        return select(relation, new ListProjection<>(projection));
    }

    /**
     * Logs the currently projected value.
     * @return a projection that will cause the projected node's value to be logged
     */
    static <T> AndProjection<T> inspect() {
        return InspectProjection::new;
    }
}
