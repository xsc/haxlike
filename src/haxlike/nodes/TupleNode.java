package haxlike.nodes;

import fj.data.HashMap;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import lombok.Value;

@Value
public class TupleNode implements Node<List<?>> {
    List<Node<?>> elements;
    boolean resolved;

    public TupleNode(List<Node<?>> elements) {
        this.elements = elements;
        this.resolved = elements.forall(Node::isResolved);
    }

    @Override
    public List<Resolvable<?>> getResolvables() {
        return elements.bind(Node::getResolvables);
    }

    @Override
    public <V> Node<List<?>> injectValues(HashMap<Resolvable<V>, V> results) {
        final TupleNode newNode = new TupleNode(
            elements.map(node -> node.injectValues(results))
        );

        return ValueNode.ifResolved(newNode);
    }

    @Override
    public List<?> getValue() {
        return elements.map(Node::getValue);
    }
}
