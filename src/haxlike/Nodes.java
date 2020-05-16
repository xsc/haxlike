package haxlike;

import fj.F;
import fj.data.List;
import haxlike.nodes.ListNode;
import haxlike.nodes.PairNode;
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
     * Combine two nodes, allowing us to call a two-parameter function on it.
     * @param <A> first value class
     * @param <B> second value class
     * @param a first value
     * @param b second value
     * @return node representing the pair of values
     */
    public static <A, B> PairNode<A, B> with(Node<A> a, Node<B> b) {
        return new PairNode<>(a, b);
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

    private Nodes() {}
}
