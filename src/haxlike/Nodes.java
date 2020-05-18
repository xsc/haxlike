package haxlike;

import fj.*;
import fj.data.List;
import haxlike.nodes.ListNode;
import haxlike.nodes.TupleNode;
import haxlike.nodes.ValueNode;

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

    // --- Mappers
    @SuppressWarnings("unchecked")
    public static <A, B, R> Node<R> map(F2<A, B, R> f, Node<A> a, Node<B> b) {
        return tuple(a, b).map(xs -> f.f((A) xs.index(0), (B) xs.index(1)));
    }

    @SuppressWarnings("unchecked")
    public static <A, B, R> Node<R> flatMap(
        F2<A, B, Node<R>> f,
        Node<A> a,
        Node<B> b
    ) {
        return tuple(a, b).flatMap(xs -> f.f((A) xs.index(0), (B) xs.index(1)));
    }

    @SuppressWarnings("unchecked")
    public static <A, B, C, R> Node<R> map(
        F3<A, B, C, R> f,
        Node<A> a,
        Node<B> b,
        Node<C> c
    ) {
        return tuple(a, b, c)
            .map(xs -> f.f((A) xs.index(0), (B) xs.index(1), (C) xs.index(2)));
    }

    @SuppressWarnings("unchecked")
    public static <A, B, C, R> Node<R> flatMap(
        F3<A, B, C, Node<R>> f,
        Node<A> a,
        Node<B> b,
        Node<C> c
    ) {
        return tuple(a, b, c)
            .flatMap(
                xs -> f.f((A) xs.index(0), (B) xs.index(1), (C) xs.index(2))
            );
    }

    /**
     * Hacky tuple node to allow multi-arg function calls.
     * @param nodes the nodes to use as function parameters
     * @return a tuple node.
     */
    private static TupleNode tuple(Node<?>... nodes) {
        final List<Node<?>> nodeList = List.arrayList(nodes);
        return new TupleNode(nodeList);
    }

    private Nodes() {}
}
