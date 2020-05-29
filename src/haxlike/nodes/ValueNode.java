package haxlike.nodes;

import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;
import lombok.Value;

@Value
public class ValueNode<T> implements Node<T> {
    T value;

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return List.nil();
    }

    @Override
    public <V> Node<T> injectValues(
        Results<? extends Resolvable<V>, V> results
    ) {
        return this;
    }

    public static <V> Node<V> ifResolved(Node<V> node) {
        return node.isResolved() ? new ValueNode<>(node.getValue()) : node;
    }
}
