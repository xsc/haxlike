package haxlike.projections;

import haxlike.Node;
import haxlike.Projection;

/**
 * Base projection that will not alter the node but can be used as
 * the basis for chaining more projections.
 * @param <T> node value class
 */
public class IdentityProjection<T> implements Projection<T> {

    @Override
    public Node<T> project(Node<T> node) {
        return node;
    }
}
