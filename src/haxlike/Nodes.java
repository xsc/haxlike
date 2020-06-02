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

    // --- Lists
    /**
     * Create a literal value node from a list of values.
     * @param <T> element value cleass
     * @param values values to wrap
     * @return a decorated list node
     */
    public static <T> ListNode<T> value(List<T> values) {
        return new ListNode.Decorator<>(new ValueNode<>(values));
    }

    /**
     * Create a literal value node from a list of values.
     * @param <T> element value cleass
     * @param values values to wrap
     * @return a decorated list node
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ListNode<T> value(T... values) {
        return new ListNode.Decorator<>(
            new ValueNode<>(List.arrayList(values))
        );
    }

    /**
     * Create list node consisting of other nodes.
     * @param <T> class of the elements
     * @param elements element nodes
     * @return node representing a list of the given values.
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> ListNode<T> list(Node<T>... elements) {
        return asList(new CollectionNode<>(List.arrayList(elements)));
    }

    /**
     * Decorate node with list-specific functionality, like {@link ListNode#traverse(F)}.
     * @param <T> element value cleass
     * @param node node to decorate
     * @return a decorated list node
     */
    public static <T> ListNode<T> asList(Node<List<T>> node) {
        return new ListNode.Decorator<>(node);
    }

    /**
     * Traverse node using the given function
     * @param <T> element value class
     * @param <R> result value class
     * @param node node to traverse
     * @return a traversed list node
     */
    public static <T, R> ListNode<R> flatMapEach(
        Node<List<T>> node,
        F<T, Node<R>> f
    ) {
        return asList(node).flatMapEach(f);
    }

    /**
     * Traverse values using the given function
     * @param <T> element value class
     * @param <R> result value class
     * @param node node to traverse
     * @return a traversed list node
     */
    public static <T, R> ListNode<R> flatMapEach(
        List<T> values,
        F<T, Node<R>> f
    ) {
        return value(values).flatMapEach(f);
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
