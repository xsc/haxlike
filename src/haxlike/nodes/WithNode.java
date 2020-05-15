package haxlike.nodes;

import static haxlike.nodes.WithNode.Pair;

import haxlike.Node;
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
public class WithNode<A, B> implements Node<Pair<A, B>> {
    Node<A> a;
    Node<B> b;
    boolean resolved;

    public WithNode(Node<A> a, Node<B> b) {
        this.a = a;
        this.b = b;
        this.resolved = a.isResolved() && b.isResolved();
    }

    @Override
    public Pair<A, B> getValue() {
        return new Pair<>(a.getValue(), b.getValue());
    }

    @Override
    public List<Resolvable<?>> allResolvables() {
        return List
            .of(a, b)
            .stream()
            .flatMap(node -> node.allResolvables().stream())
            .collect(Collectors.toList());
    }

    @Override
    public <V> Node<Pair<A, B>> injectValue(Resolvable<V> resolvable, V value) {
        WithNode<A, B> newNode = new WithNode<>(
            a.injectValue(resolvable, value),
            b.injectValue(resolvable, value)
        );
        return ValueNode.ifResolved(newNode);
    }

    public <R> Node<R> map(BiFunction<A, B, R> f) {
        return this.map(pair -> f.apply(pair.getA(), pair.getB()));
    }

    public <R> Node<R> flatMap(BiFunction<A, B, Node<R>> f) {
        return this.flatMap(pair -> f.apply(pair.getA(), pair.getB()));
    }

    // --- Pair, container for tuple
    @Value
    public static class Pair<A, B> {
        A a;
        B b;
    }
}
