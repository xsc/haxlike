package haxlike.projections;

import haxlike.Node;
import haxlike.Nodes;
import haxlike.Projection;
import haxlike.Relation;
import haxlike.Resolvable;
import lombok.NonNull;
import lombok.Value;

/**
 * Projection that will cause relations to be resolved for the
 * given relation name. The relation's node will be projected
 * as well, using the given sub-projection.
 * @param <T> node value class
 * @param <R> relation value class
 */
@Value
public class SelectProjection<T, V, R extends Resolvable<V>>
    implements Projection<T> {
    @NonNull
    Projection<T> base;

    @NonNull
    Relation<T, V, R> relation;

    @NonNull
    Projection<V> projection;

    @Override
    public Node<T> project(Node<T> node) {
        final Node<T> inner = base.project(node);
        return Nodes
            .tuple(inner, createRelationNode(node))
            .map(this::attachRelation);
    }

    private Node<V> createRelationNode(Node<T> node) {
        return projection.project(node.flatMap(relation.getNodeFunction()));
    }

    private T attachRelation(T value, V relationResult) {
        return relation.getAttachFunction().f(value, relationResult);
    }
}
