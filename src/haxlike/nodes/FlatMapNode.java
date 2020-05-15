package haxlike.nodes;

import haxlike.Node;
import haxlike.Resolvable;
import java.util.List;
import java.util.function.Function;
import lombok.Value;

@Value
public class FlatMapNode<T, R> implements Node.WithoutValue<R> {
    Node<T> inner;
    Function<T, Node<R>> f;

    @Override
    public List<Resolvable<?, ?>> allResolvables() {
        return inner.allResolvables();
    }

    @Override
    public <V, I extends Resolvable<V, I>> Node<R> injectValue(
        I resolvable,
        V value
    ) {
        final Node<T> result = inner.injectValue(resolvable, value);
        return result.hasValue()
            ? f.apply(result.getValue())
            : new FlatMapNode<>(result, f);
    }
}
