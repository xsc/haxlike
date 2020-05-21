package haxlike.nodes.decorators;

import fj.F;
import fj.F2;
import fj.data.List;
import haxlike.Node;
import haxlike.nodes.CollectionNode;

public class ListDecorator<T> extends NodeDecorator<List<T>> {

    public ListDecorator(Node<List<T>> inner) {
        super(inner);
    }

    public <R> ListDecorator<R> collect(F<T, R> f) {
        return new ListDecorator<>(this.map(xs -> xs.map(f)));
    }

    public <R> ListDecorator<R> traverse(F<T, Node<R>> f) {
        return new ListDecorator<>(
            this.flatMap(xs -> new CollectionNode<>(xs.map(f)))
        );
    }

    public <R> Node<R> foldLeft(F2<R, T, R> f, R acc) {
        return this.map(xs -> xs.foldLeft(f, acc));
    }

    public <R> Node<R> foldRight(F2<T, R, R> f, R acc) {
        return this.map(xs -> xs.foldRight(f, acc));
    }
}
