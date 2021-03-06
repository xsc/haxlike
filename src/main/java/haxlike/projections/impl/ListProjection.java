package haxlike.projections.impl;

import fj.data.List;
import haxlike.Node;
import haxlike.Nodes;
import haxlike.projections.Projection;
import lombok.NonNull;
import lombok.Value;

/**
 * Projection that will cause the given sub-projection to be
 * applied to every element of a node's list
 * @param <T> list element class
 */
@Value
public class ListProjection<T> implements Projection<List<T>> {
    @NonNull
    Projection<T> projection;

    @Override
    public Node<List<T>> project(Node<List<T>> node) {
        return Nodes.asList(node).traverse(projection::project);
    }
}
