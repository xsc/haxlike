package haxlike.nodes.tuples;

import static fj.P.p;

import fj.F4;
import fj.P4;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;
import haxlike.nodes.ValueNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Tuple4<A, B, C, D> implements Node<P4<A, B, C, D>> {
    Node<A> a;
    Node<B> b;
    Node<C> c;
    Node<D> d;
    boolean resolved;
    List<Resolvable<?>> resolvables;

    public Tuple4(
        final Node<A> a,
        final Node<B> b,
        final Node<C> c,
        final Node<D> d
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.resolved =
            a.isResolved() &&
            b.isResolved() &&
            c.isResolved() &&
            d.isResolved();
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
    public P4<A, B, C, D> getValue() {
        return p(a.getValue(), b.getValue(), c.getValue(), d.getValue());
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return resolvables;
    }

    @Override
    public <V> Node<P4<A, B, C, D>> injectValues(
        Results<? extends Resolvable<V>, V> results
    ) {
        return ValueNode.ifResolved(
            new Tuple4<>(
                a.injectValues(results),
                b.injectValues(results),
                c.injectValues(results),
                d.injectValues(results)
            )
        );
    }

    public <R> Node<R> map(F4<A, B, C, D, R> f) {
        return this.map(p -> f.f(p._1(), p._2(), p._3(), p._4()));
    }

    public <R> Node<R> flatMap(F4<A, B, C, D, Node<R>> f) {
        return this.flatMap(p -> f.f(p._1(), p._2(), p._3(), p._4()));
    }

    @Override
    public String toString() {
        return (
            a.toString() +
            ",\n" +
            b.toString() +
            ",\n" +
            c.toString() +
            ",\n" +
            d.toString()
        );
    }
}
