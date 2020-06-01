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

    /**
     * Alias for {@link ListDecorator#flatMapEach}.
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results.
     */
    public <R> ListDecorator<R> traverse(F<T, Node<R>> f) {
        return this.flatMapEach(f);
    }

    /**
     * Apply the given function to each element of the list, resulting in a new
     * collection of new nodes to resolve.
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results
     */
    public <R> ListDecorator<R> flatMapEach(F<T, Node<R>> f) {
        return new ListDecorator<>(
            this.flatMap(xs -> new CollectionNode<>(xs.map(f)))
        );
    }

    /**
     * Apply the given function to each element of the list
     * @param <R> result value class
     * @param f mapper function
     * @return a node containing a list of results
     */
    public <R> ListDecorator<R> mapEach(F<T, R> f) {
        return new ListDecorator<>(this.map(xs -> xs.map(f)));
    }

    /**
     * Run the given 'fetch' function for each element of this list to create
     * a value to attach. Afterwards call the given 'attach' function on the
     * original value and the value to attach.
     * @param <V> result value class
     * @param <R> attachment value class
     * @param attach function to combine the original value with the attachment
     * @param attachment function to generate the attachment
     * @return node containing a list where every element is the combination of the original element and an attachment.
     */
    public <V, R> ListDecorator<V> attachEach(
        F2<T, R, V> attach,
        F<T, Node<R>> attachment
    ) {
        return this.flatMapEach(
                value ->
                    attachment.f(value).map(result -> attach.f(value, result))
            );
    }

    public <R> Node<R> foldLeft(F2<R, T, R> f, R acc) {
        return this.map(xs -> xs.foldLeft(f, acc));
    }

    public <R> Node<R> foldRight(F2<T, R, R> f, R acc) {
        return this.map(xs -> xs.foldRight(f, acc));
    }
}
