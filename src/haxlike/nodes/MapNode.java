package haxlike.nodes;

import fj.F;
import fj.data.List;
import haxlike.Node;
import haxlike.PlainNode;
import haxlike.Resolvable;
import haxlike.resolvers.Results;
import lombok.Value;

@Value
public class MapNode<T, R> implements Node<R> {
    PlainNode<T> inner;
    F<T, R> f;

    @Override
    public List<Resolvable<?>> getResolvables() {
        return inner.getResolvables();
    }

    @Override
    public PlainNode<R> injectValues(Results<Resolvable<?>, ?> results) {
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
