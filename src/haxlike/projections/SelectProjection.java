package haxlike.projections;

import haxlike.Node;
import haxlike.Nodes;
import haxlike.Projection;
import haxlike.Relation;
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
public class SelectProjection<T, R> implements Projection<T> {
    @NonNull
    Projection<T> base;

    @NonNull
    Relation<T, R> relation;

    @NonNull
    Projection<R> projection;

    @Override
    public Node<T> project(Node<T> node) {
        return base.project(node).flatMap(this::attachRelation);
    }

    private Node<T> attachRelation(T value) {
        final Node<R> relNode = relation.getNodeFunction().f(value);
        return Nodes
            .tuple(Nodes.value(value), projection.project(relNode))
            .map(relation.getAttachFunction());
    }
}
