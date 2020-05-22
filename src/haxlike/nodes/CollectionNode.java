package haxlike.nodes;

import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;
import lombok.Value;

@Value
public class CollectionNode<T> implements Node<List<T>> {
    List<Node<T>> elements;
    boolean resolved;

    public CollectionNode(List<Node<T>> elements) {
        this.elements = elements;
        this.resolved = elements.forall(Node::isResolved);
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return elements.bind(Node::getResolvables);
    }

    @Override
    public <V> Node<List<T>> injectValues(
        Results<? extends Resolvable<V>, V> results
    ) {
        final CollectionNode<T> newNode = new CollectionNode<>(
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
