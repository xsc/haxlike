package haxlike;

import fj.F;
import fj.F2;
import fj.F3;
import fj.data.List;
import haxlike.nodes.*;
import haxlike.nodes.tuples.*;
import haxlike.projections.Projection;
import haxlike.resolvers.Results;

/**
 * Representation of a, potentially nested, node eventually containing a value.
 * @param <T> the class of the value contained in the node.
 */
@SuppressWarnings("squid:S1452")
public interface Node<T> {
    /**
     * Check if this node is fully resolved.
     * @return true if fully resolved.
     */
    boolean isResolved();

    /**
     * Retrieve the value contained in this node. This needs to be guarded by
     * {@link Node#isResolved()}.
     * @return the value contained in the node.
     */
    T getValue();

    /**
     * Retrieve a list of all resolvables (including nested ones) from the node.
     * @return the list of resolvables.
     */
    List<Resolvable<?>> getResolvables();

    /**
     * Given a map of resolvables and their results, inject them into the node,
     * resulting in a partially or fully resolved node with the same value type.
     * @param <V> generic class representing resolvable results.
     * @param results map of resolvables and their results.
     * @return a partially or fully resolved node with the same value type.
     */
    Node<T> injectValues(Results<Resolvable<?>, ?> results);

    // ========================= PROJECTION =========================
    /**
     * Apply the given projection to this node.
     * @param projection projection to apply
     * @return node with the projection applied
     */
    default Node<T> project(Projection<T> projection) {
        return projection.project(this);
    }

    // ========================= COMPOSITION =========================
    /**
     * Apply the given function to the content of the node.
     * @param <R> return type of the function.
     * @param f the function to apply.
     * @return a new node containing the result of applying the function.
     */
    default <R> Node<R> map(F<T, R> f) {
        return new MapNode<>(this, f);
    }

    /**
     * Apply the given function to the content of the node, resulting in a new node.
     * @param <R> the class of the new node's value.
     * @param f the function to apply.
     * @return a new node representing the result of applying the function.
     */
    default <R> Node<R> flatMap(F<T, ? extends Node<R>> f) {
        if (this.isResolved()) {
            return new FlatMapNode.Resolved<>(this, f);
        } else {
            return new FlatMapNode<>(this, f);
        }
    }

    /**
     * Like {@link Node#map(F)} but takes an additional argument.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node)}:
     * <pre>
     * Nodes.tuple(node, other).map(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node containing the result of applying the function.
     */
    default <A, R> Node<R> map(F2<T, A, R> f, Node<A> other) {
        return Nodes.tuple(this, other).map(f);
    }

    /**
     * Like {@link Node#flatMap(F)} but takes an additional argument.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node)}:
     * <pre>
     * Nodes.tuple(node, other).flatMap(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node representing the result of applying the function.
     */
    default <A, R> Node<R> flatMap(F2<T, A, Node<R>> f, Node<A> other) {
        return Nodes.tuple(this, other).flatMap(f);
    }

    /**
     * Like {@link Node#map(F)} but takes two additional arguments.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node, Node)}:
     * <pre>
     * Nodes.tuple(node, a, b).map(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <B> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node containing the result of applying the function.
     */
    default <A, B, R> Node<R> map(F3<T, A, B, R> f, Node<A> a, Node<B> b) {
        return Nodes.tuple(this, a, b).map(f);
    }

    /**
     * Like {@link Node#flatMap(F)} but takes two additional arguments.
     * <p />
     * This is identical to using {@link Nodes#tuple(Node, Node, Node)}:
     * <pre>
     * Nodes.tuple(node, a, b).flatMap(f)
     * </pre>
     *
     * @param <A> additional parameter class
     * @param <B> additional parameter class
     * @param <R> return value class
     * @param f function to apply to this node and the additional parameter
     * @param other node containing the additional parameter
     * @return node representing the result of applying the function.
     */
    default <A, B, R> Node<R> flatMap(
        F3<T, A, B, Node<R>> f,
        Node<A> a,
        Node<B> b
    ) {
        return Nodes.tuple(this, a, b).flatMap(f);
    }

    // ========================= JUXT =========================
    /**
     * Create a tuple with two elements, each representing the application
     * of one of the given functions on the current node's value.
     * @param <A> result value class
     * @param <B> result value class
     * @param fa first function to apply
     * @param fb second function to apply
     * @return a tuple of two elements
     */
    default <A, B> Tuple2<A, B> juxt(F<T, Node<A>> fa, F<T, Node<B>> fb) {
        return Nodes.tuple(this.flatMap(fa), this.flatMap(fb));
    }

    /**
     * Create a tuple with three elements, each representing the application
     * of one of the given functions on the current node's value.
     * @param <A> result value class
     * @param <B> result value class
     * @param <C> result value class
     * @param fa first function to apply
     * @param fb second function to apply
     * @param fc third function to apply
     * @return a tuple of three elements
     */
    default <A, B, C> Tuple3<A, B, C> juxt(
        F<T, Node<A>> fa,
        F<T, Node<B>> fb,
        F<T, Node<C>> fc
    ) {
        return Nodes.tuple(
            this.flatMap(fa),
            this.flatMap(fb),
            this.flatMap(fc)
        );
    }

    /**
     * Create a tuple with two elements, the first one being the current node,
     * the other representing the application of the given function on the
     * current node's value.
     * @param <A> result value class
     * @param fa first function to apply
     * @return a tuple of two elements
     */
    default <A> Tuple2<T, A> juxtSelf(F<T, Node<A>> f) {
        return Nodes.tuple(this, this.flatMap(f));
    }

    /**
     * Create a tuple with three elements, the first one being the current node,
     * the others each representing the application of one of the given functions
     * on the current node's value.
     * @param <A> result value class
     * @param <B> result value class
     * @param fa first function to apply
     * @param fb second function to apply
     * @return a tuple of three elements
     */
    default <A, B> Tuple3<T, A, B> juxtSelf(
        F<T, Node<A>> fa,
        F<T, Node<B>> fb
    ) {
        return Nodes.tuple(this, this.flatMap(fa), this.flatMap(fb));
    }

    // ========================= ATTACH =========================
    /**
     * Run the given attachment function for this node's value to create a value to attach
     * Afterwards call the given 'attach' function on the original value and the value to attach.
     * @param <V> result value class
     * @param <R> attachment value class
     * @param attach function to combine the original value with the attachment
     * @param attachment function to generate the attachment
     * @return node containing a list where every element is the combination of
     * the original element and an attachment.
     */
    default <V, R> Node<V> attach(
        F2<T, R, V> attach,
        F<T, Node<R>> attachment
    ) {
        return Nodes.tuple(this, this.flatMap(attachment)).map(attach);
    }

    /**
     * Run the given attachment functions for this node's value to create values to attach
     * Afterwards call the given 'attach' function on the original value and the values to attach.
     * @param <V> result value class
     * @param <A> first attachment value class
     * @param <B> second attachment value class
     * @param attach function to combine the original value with the attachment
     * @param firstAttachment function to generate the first attachment
     * @param secondAttachment function to generate the second attachment
     * @return node containing a list where every element is the combination of
     * the original element and an attachment.
     */
    default <V, A, B> Node<V> attach(
        F3<T, A, B, V> attach,
        F<T, Node<A>> firstAttachment,
        F<T, Node<B>> secondAttachment
    ) {
        return Nodes
            .tuple(
                this,
                this.flatMap(firstAttachment),
                this.flatMap(secondAttachment)
            )
            .map(attach);
    }

    /**
     * Trait to enrich a {@link Node} containing a list with useful list traversal functions.
     * @param <T> list element class
     */
    public static interface ListNode<T> extends Node<List<T>> {
        /**
         * Similar to {@link ListNode#flatMapEach} but will pass {@link Node} values
         * to the function. This allows for a more fluid style when building up resolvable
         * trees.
         * @param <R> result value class
         * @param f mapper function
         * @return a node containing a list of results.
         */
        default <R> ListNode<R> traverse(F<Node<T>, ? extends Node<R>> f) {
            return this.flatMapEach(value -> f.f(Nodes.value(value)));
        }

        /**
         * Similar to {@link ListNode#mapEach} but will pass {@link Node} values
         * to the function. This allows for a more fluid style when building up resolvable
         * trees.
         * @param <R> result value class
         * @param f mapper function
         * @return a node containing a list of results.
         */
        default <R> ListNode<R> collect(F<Node<T>, R> f) {
            return this.mapEach(value -> f.f(Nodes.value(value)));
        }

        /**
         * Apply the given function to each element of the list, resulting in a new
         * collection of new nodes to resolve.
         * @param <R> result value class
         * @param f mapper function
         * @return a node containing a list of results
         */
        default <R> ListNode<R> flatMapEach(F<T, ? extends Node<R>> f) {
            return new Decorator<>(
                this.flatMap(xs -> new CollectionNode<>(xs.map(f)))
            );
        }

        /**
         * Apply the given function to each element of the list
         * @param <R> result value class
         * @param f mapper function
         * @return a node containing a list of results
         */
        default <R> ListNode<R> mapEach(F<T, R> f) {
            return new Decorator<>(this.map(xs -> xs.map(f)));
        }

        /**
         * Apply left fold function across this list's element.
         * @param <R> accumulator class
         * @param f fold function
         * @param acc accumulator
         * @return result of applying the fold function to the accumulator and every element.
         */
        default <R> Node<R> foldLeft(F2<R, T, R> f, R acc) {
            return this.map(xs -> xs.foldLeft(f, acc));
        }

        /**
         * Apply right fold function across this list's element.
         * @param <R> accumulator class
         * @param f fold function
         * @param acc accumulator
         * @return result of applying the fold function to the accumulator and every element.
         */
        default <R> Node<R> foldRight(F2<T, R, R> f, R acc) {
            return this.map(xs -> xs.foldRight(f, acc));
        }

        public static <T> ListNode<T> decorate(Node<List<T>> node) {
            return new Decorator<>(node);
        }

        /**
         * Run the given attachment function for each element of this list to create
         * a value to attach. Afterwards call the given 'attach' function on the
         * original value and the value to attach.
         * @param <V> result value class
         * @param <R> attachment value class
         * @param attach function to combine the original value with the attachment
         * @param attachment function to generate the attachment
         * @return node containing a list where every element is the combination of the original element and an attachment.
         */
        default <V, R> ListNode<V> attachEach(
            F2<T, R, V> attach,
            F<T, Node<R>> attachment
        ) {
            return this.flatMapEach(
                    value -> Nodes.value(value).attach(attach, attachment)
                );
        }

        /**
         * Run the given attachment functions for each element of this list to create
         * values to attach. Afterwards call the given 'attach' function on the
         * original value and the values to attach.
         * @param <V> result value class
         * @param <A> first attachment value class
         * @param <B> second attachment value class
         * @param attach function to combine the original value with the attachment
         * @param firstAttachment function to generate the first attachment
         * @param secondAttachment function to generate the second attachment
         * @return node containing a list where every element is the combination of
         * the original element and an attachment.
         */
        default <V, A, B> ListNode<V> attachEach(
            F3<T, A, B, V> attach,
            F<T, Node<A>> firstAttachment,
            F<T, Node<B>> secondAttachment
        ) {
            return this.flatMapEach(
                    value ->
                        Nodes
                            .tuple(
                                firstAttachment.f(value),
                                secondAttachment.f(value)
                            )
                            .map((a, b) -> attach.f(value, a, b))
                );
        }

        /**
         * Simple decorator to add list traversal functionality to any list-containing
         * {@link Node}.
         * @param <T> element class.
         */
        static class Decorator<T>
            extends NodeDecorator<List<T>>
            implements ListNode<T> {

            Decorator(Node<List<T>> inner) {
                super(inner);
            }
        }
    }

    /**
     * Convenience trait for values that need to be combinable with nodes. Avoids
     * explicit wrapping using {@link Nodes#value(Object)}.
     *
     * @param <T> self-reference for value class
     */
    public interface Data<T extends Data<T>> extends Node<T> {
        @Override
        @SuppressWarnings("unchecked")
        default T getValue() {
            return (T) this;
        }

        @Override
        default boolean isResolved() {
            return true;
        }

        @Override
        default List<Resolvable<?>> getResolvables() {
            return List.nil();
        }

        @Override
        default Node<T> injectValues(Results<Resolvable<?>, ?> results) {
            return this;
        }
    }
}
