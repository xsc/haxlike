package haxlike.nodes;

import fj.data.HashMap;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import lombok.Value;

@Value
public class ListNode<T> implements Node<List<T>> {
    List<Node<T>> elements;
    boolean resolved;

    public ListNode(List<Node<T>> elements) {
        this.elements = elements;
        this.resolved = elements.forall(Node::isResolved);
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return elements.bind(Node::getResolvables);
    }

    @Override
    public <V> Node<List<T>> injectValues(HashMap<Resolvable<V>, V> results) {
        final ListNode<T> newNode = new ListNode<>(
            elements.map(node -> node.injectValues(results))
        );

        return ValueNode.ifResolved(newNode);
    }

    @Override
    public List<T> getValue() {
        return elements.map(Node::getValue);
    }

    @Override
    public String toString() {
        return (
            "[\n" +
            Printer.indent(
                String.join(
                    ",\n  ",
                    elements.map(Object::toString).toJavaList()
                )
            ) +
            "\n]"
        );
    }
}
