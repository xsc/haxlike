package haxlike.nodes;

import static fj.P.p;

import fj.F2;
import fj.P2;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import lombok.Value;

/**
 * Node representing the combination of two nodes, allowing a call to map/flatMap with
 * a two parameter function.
 * @param <A> first value class
 * @param <B> second value class
 */
@Value
public class PairNode<A, B> implements Node<P2<A, B>> {
    Node<A> a;
    Node<B> b;
    List<Resolvable<?>> resolvables;
    boolean resolved;

    public PairNode(Node<A> a, Node<B> b) {
        this.a = a;
        this.b = b;
        this.resolvables = a.getResolvables().append(b.getResolvables());
        this.resolved = a.isResolved() && b.isResolved();
    }

    @Override
    public P2<A, B> getValue() {
        return p(a.getValue(), b.getValue());
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return resolvables;
    }

    @Override
    public <V> Node<P2<A, B>> injectValue(Resolvable<V> resolvable, V value) {
        PairNode<A, B> newNode = new PairNode<>(
            a.injectValue(resolvable, value),
            b.injectValue(resolvable, value)
        );
        return ValueNode.ifResolved(newNode);
    }

    public <R> Node<R> map(F2<A, B, R> f) {
        return this.map(pair -> f.f(pair._1(), pair._2()));
    }

    public <R> Node<R> flatMap(F2<A, B, Node<R>> f) {
        return this.flatMap(pair -> f.f(pair._1(), pair._2()));
    }
}
