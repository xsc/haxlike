package haxlike.nodes;

import fj.F;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;
import lombok.Value;

@Value
public class MapNode<T, R> implements Node<R> {
    Node<T> inner;
    F<T, R> f;

    @Override
    public List<Resolvable<?>> getResolvables() {
        return inner.getResolvables();
    }

    @Override
    public Node<R> injectValues(Results results) {
        return new MapNode<>(inner.injectValues(results), f);
    }

    @Override
    public boolean isResolved() {
        return inner.isResolved();
    }

    @Override
    public R getValue() {
        return f.f(inner.getValue());
    }
}
