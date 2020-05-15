package haxlike.nodes;

import haxlike.Node;
import haxlike.Resolvable;
import java.util.List;
import java.util.function.Function;
import lombok.Value;

@Value
public class FlatMapNode<T, R> implements Node<R> {
    Node<T> inner;
    Function<T, Node<R>> f;

    @Override
    public List<Resolvable<?>> allResolvables() {
        return inner.allResolvables();
    }

    @Override
    public <V> Node<R> injectValue(Resolvable<V> resolvable, V value) {
        final Node<T> result = inner.injectValue(resolvable, value);
        return result.isResolved()
            ? f.apply(result.getValue())
            : new FlatMapNode<>(result, f);
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public R getValue() {
        throw new UnsupportedOperationException(
            "Cannot call 'getValue' on flatMap node."
        );
    }
}
