package haxlike.nodes.decorators;

import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;

/**
 * Decorator for nodes, passing all calls to the wrapped node.
 * @param <T> value class of the underlying node.
 */
public class NodeDecorator<T> implements Node<T> {
    private final Node<T> inner;

    public NodeDecorator(Node<T> inner) {
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
    public <V> Node<T> injectValues(
        Results<? extends Resolvable<V>, V> results
    ) {
        return inner.injectValues(results);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
