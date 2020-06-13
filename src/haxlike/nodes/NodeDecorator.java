package haxlike.nodes;

import fj.data.List;
import haxlike.Node;
import haxlike.PlainNode;
import haxlike.Resolvable;
import haxlike.resolvers.Results;

/**
 * Decorator for nodes, passing all calls to the wrapped node.
 *
 * @param <T> value class of the underlying node.
 */
public class NodeDecorator<T> implements Node<T> {
    private final PlainNode<T> inner;

    public NodeDecorator(PlainNode<T> inner) {
        this.inner = inner;
    }

    @Override
    public boolean isResolved() {
        return inner.isResolved();
    }

    @Override
    public T getValue() {
        return inner.getValue();
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return inner.getResolvables();
    }

    @Override
    public PlainNode<T> injectValues(Results<Resolvable<?>, ?> results) {
        return inner.injectValues(results);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
