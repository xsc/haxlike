package haxlike;

import haxlike.nodes.ListNode;
import haxlike.nodes.ValueNode;
import haxlike.nodes.WithNode;
import java.util.List;

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
        return new ListNode<>(List.of(elements));
    }

    /**
     * Combine two nodes, allowing us to call a two-parameter function on it.
     * @param <A> first value class
     * @param <B> second value class
     * @param a first value
     * @param b second value
     * @return node representing the pair of values
     */
    public static <A, B> WithNode<A, B> with(Node<A> a, Node<B> b) {
        return new WithNode<>(a, b);
    }

    private Nodes() {}
}
