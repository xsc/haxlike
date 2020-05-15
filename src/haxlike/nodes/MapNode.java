package haxlike.nodes;

import haxlike.Node;
import haxlike.Resolvable;
import java.util.List;
import java.util.function.Function;
import lombok.Value;

@Value
public class MapNode<T, R> implements Node<R> {
    Node<T> inner;
    Function<T, R> f;

    @Override
    public List<Resolvable<?, ?>> allResolvables() {
        return inner.allResolvables();
    }

    @Override
    public <V, I extends Resolvable<V, I>> Node<R> injectValue(
        I resolvable,
        V value
    ) {
        return new MapNode<>(inner.injectValue(resolvable, value), f);
    }

    @Override
    public boolean hasValue() {
        return inner.hasValue();
    }

    @Override
    public R getValue() {
        return f.apply(inner.getValue());
    }
}
