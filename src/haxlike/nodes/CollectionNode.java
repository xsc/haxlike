package haxlike.nodes;

import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;
import lombok.Value;

@Value
public class CollectionNode<T> implements Node<List<T>> {
    List<Node<T>> childNodes;
    boolean resolved;

    public CollectionNode(List<Node<T>> childNodes) {
        this.childNodes = childNodes;
        this.resolved = childNodes.forall(Node::isResolved);
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return childNodes.bind(Node::getResolvables);
    }

    @Override
    public Node<List<T>> injectValues(Results results) {
        final CollectionNode<T> newNode = new CollectionNode<>(
            childNodes.map(node -> node.injectValues(results))
        );

        return ValueNode.ifResolved(newNode);
    }

    @Override
    public List<T> getValue() {
        return childNodes.map(Node::getValue);
    }
}
