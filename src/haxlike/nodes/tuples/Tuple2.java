package haxlike.nodes.tuples;

import static fj.P.p;

import fj.F2;
import fj.P2;
import fj.data.List;
import haxlike.Node;
import haxlike.PlainNode;
import haxlike.Resolvable;
import haxlike.nodes.ValueNode;
import haxlike.resolvers.Results;
import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Tuple2<A, B> implements Node<P2<A, B>> {
    PlainNode<A> a;
    PlainNode<B> b;
    boolean resolved;
    List<Resolvable<?>> resolvables;

    public Tuple2(PlainNode<A> a, PlainNode<B> b) {
        this.a = a;
        this.b = b;
        this.resolved = a.isResolved() && b.isResolved();
        this.resolvables = a.getResolvables().append(b.getResolvables());
    }

    @Override
    public boolean isResolved() {
        return resolved;
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
    public Node<P2<A, B>> injectValues(Results<Resolvable<?>, ?> results) {
        return ValueNode.ifResolved(
            new Tuple2<>(a.injectValues(results), b.injectValues(results))
        );
    }

    public <R> Node<R> map(F2<A, B, R> f) {
        return this.map(p -> f.f(p._1(), p._2()));
    }

    public <R> Node<R> flatMap(F2<A, B, PlainNode<R>> f) {
        return this.flatMap(p -> f.f(p._1(), p._2()));
    }
}
