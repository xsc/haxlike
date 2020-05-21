package haxlike;

import fj.*;
import fj.data.List;
import haxlike.nodes.*;
import haxlike.nodes.tuples.*;

public class Nodes {

    /**
     * Create literal value node.
     * @param <T> value class
     * @param v value to wrap
     * @return node containing the literal value
     */
    public static <T> Node<T> value(T v) {
        return new ValueNode<>(v);
    }

    /**
     * Create list node consisting of other nodes.
     * @param <T> class of the elements
     * @param elements element nodes
     * @return node representing a list of the given values.
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Node<List<T>> list(Node<T>... elements) {
        return new ListNode<>(List.arrayList(elements));
    }

    /**
     * Apply the given function to every element of the list contained in the
     * given node, resulting in a list of nodes to resolve.
     * @param <T> element class
     * @param <R> result element class
     * @param node node to apply function to
     * @param f function to appy
     * @return a new node containing the list of mapped elements
     */
    public static <T, R> Node<List<R>> traverse(
        Node<List<T>> node,
        F<T, Node<R>> f
    ) {
        return node.map(elements -> elements.map(f)).flatMap(ListNode::new);
    }

    // --- Tuples
    public static <A, B> Tuple2<A, B> tuple(Node<A> a, Node<B> b) {
        return new Tuple2<>(a, b);
    }

    public static <A, B, C> Tuple3<A, B, C> tuple(
        Node<A> a,
        Node<B> b,
        Node<C> c
    ) {
        return new Tuple3<>(a, b, c);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> tuple(
        Node<A> a,
        Node<B> b,
        Node<C> c,
        Node<D> d
    ) {
        return new Tuple4<>(a, b, c, d);
    }

    private Nodes() {}
}
