package haxlike.nodes.tuples;

import static fj.P.p;

import fj.F3;
import fj.P3;
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
public final class Tuple3<A, B, C> implements Node<P3<A, B, C>> {
    PlainNode<A> a;
    PlainNode<B> b;
    PlainNode<C> c;
    boolean resolved;
    List<Resolvable<?>> resolvables;

    public Tuple3(
        final PlainNode<A> a,
        final PlainNode<B> b,
        final PlainNode<C> c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.resolved = a.isResolved() && b.isResolved() && c.isResolved();
        this.resolvables =
            a
                .getResolvables()
                .append(b.getResolvables())
                .append(c.getResolvables());
    }

    @Override
    public boolean isResolved() {
        return resolved;
    }

    @Override
    public P3<A, B, C> getValue() {
        return p(a.getValue(), b.getValue(), c.getValue());
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return resolvables;
    }

    @Override
    public PlainNode<P3<A, B, C>> injectValues(
        Results<Resolvable<?>, ?> results
    ) {
        return ValueNode.ifResolved(
            new Tuple3<>(
                a.injectValues(results),
                b.injectValues(results),
                c.injectValues(results)
            )
        );
    }

    public <R> Node<R> map(F3<A, B, C, R> f) {
        return this.map(p -> f.f(p._1(), p._2(), p._3()));
    }

    public <R> Node<R> flatMap(F3<A, B, C, PlainNode<R>> f) {
        return this.flatMap(p -> f.f(p._1(), p._2(), p._3()));
    }
}
