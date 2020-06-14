package haxlike.traits;

import fj.F;
import fj.F2;
import fj.F3;
import haxlike.Node;
import haxlike.Nodes;
import haxlike.PlainNode;

public interface AttachNode<T> extends PlainNode<T> {
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
        F<T, PlainNode<A>> firstAttachment,
        F<T, PlainNode<B>> secondAttachment
    ) {
        return Nodes
            .tuple(
                this,
                this.flatMap(firstAttachment),
                this.flatMap(secondAttachment)
            )
            .map(attach);
    }
}
