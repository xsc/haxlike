package haxlike.nodes;

import fj.data.List;
import haxlike.Node;
import haxlike.PlainNode;
import haxlike.Resolvable;
import haxlike.resolvers.Results;
import lombok.Value;

@Value
public class CollectionNode<T> implements Node<List<T>> {
    List<? extends PlainNode<T>> childNodes;
    boolean resolved;

    public CollectionNode(List<? extends PlainNode<T>> childNodes) {
        this.childNodes = childNodes;
        this.resolved = childNodes.forall(PlainNode::isResolved);
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return childNodes.bind(PlainNode::getResolvables);
    }

    @Override
    public PlainNode<List<T>> injectValues(Results<Resolvable<?>, ?> results) {
        final CollectionNode<T> newNode = new CollectionNode<>(
            childNodes.map(node -> node.injectValues(results))
        );

        return ValueNode.ifResolved(newNode);
    }

    @Override
    public List<T> getValue() {
        return childNodes.map(PlainNode::getValue);
    }
}
