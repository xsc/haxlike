package haxlike.nodes;

import haxlike.Node;
import haxlike.Nodes;
import haxlike.Resolvable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.Value;

/**
 * Node representing the combination of two nodes, allowing a call to map/flatMap with
 * a two parameter function.
 * @param <A> first value class
 * @param <B> second value class
 */
@Value
public class WithNode<A, B> implements Node<WithNode<A, B>.Container> {
    Node<A> a;
    Node<B> b;

    @Override
    public boolean hasValue() {
        return a.hasValue() && b.hasValue();
    }

    @Override
    public Container getValue() {
        return new Container(a.getValue(), b.getValue());
    }

    @Override
    public List<Resolvable<?, ?>> allResolvables() {
        return List
            .of(a, b)
            .stream()
            .flatMap(node -> node.allResolvables().stream())
            .collect(Collectors.toList());
    }

    @Override
    public <V, R extends Resolvable<V, R>> Node<Container> injectValue(
        R resolvable,
        V value
    ) {
        Node<A> resultA = a.injectValue(resolvable, value);
        Node<B> resultB = b.injectValue(resolvable, value);

        if (resultA.hasValue() && resultB.hasValue()) {
            return Nodes.value(
                new Container(resultA.getValue(), resultB.getValue())
            );
        }

        return new WithNode<>(resultA, resultB);
    }

    public <R> Node<R> map(BiFunction<A, B, R> f) {
        return this.map(
                container -> f.apply(container.getA(), container.getB())
            );
    }

    public <R> Node<R> flatMap(BiFunction<A, B, Node<R>> f) {
        return this.flatMap(
                container -> f.apply(container.getA(), container.getB())
            );
    }

    @Value
    public class Container {
        A a;
        B b;
    }
}
