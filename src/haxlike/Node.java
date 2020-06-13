package haxlike;

import haxlike.projections.Projection;
import haxlike.traits.AttachNode;
import haxlike.traits.ComposeNode;
import haxlike.traits.JuxtNode;

/**
 * Representation of a, potentially nested, node eventually containing a value.
 * @param <T> the class of the value contained in the node.
 */
public interface Node<T> extends AttachNode<T>, ComposeNode<T>, JuxtNode<T> {
    /**
     * Apply the given projection to this node.
     * @param projection projection to apply
     * @return node with the projection applied
     */
    default Node<T> project(Projection<T> projection) {
        return projection.project(this);
    }
}
