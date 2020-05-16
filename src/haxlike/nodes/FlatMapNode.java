package haxlike.nodes;

import fj.F;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import lombok.Value;

@Value
public class FlatMapNode<T, R> implements Node<R> {
    Node<T> inner;
    F<T, Node<R>> f;

    @Override
    public List<Resolvable<?>> getResolvables() {
        return inner.getResolvables();
    }

    @Override
    public <V> Node<R> injectValue(Resolvable<V> resolvable, V value) {
        final Node<T> result = inner.injectValue(resolvable, value);
        return result.isResolved()
            ? f.f(result.getValue())
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
