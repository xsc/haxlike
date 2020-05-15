package haxlike.nodes;

import haxlike.Node;
import haxlike.Resolvable;
import java.util.List;
import lombok.Value;

@Value
public class ValueNode<T> implements Node<T> {
    T value;

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    public List<Resolvable<?>> allResolvables() {
        return List.of();
    }

    @Override
    public <V> Node<T> injectValue(Resolvable<V> resolvable, V value) {
        return this;
    }

    public static <V> Node<V> ifResolved(Node<V> node) {
        return node.isResolved() ? new ValueNode<>(node.getValue()) : node;
    }
}
