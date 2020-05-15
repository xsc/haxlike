package haxlike.nodes;

import haxlike.Node;
import haxlike.Resolvable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;

@Value
public class ListNode<T> implements Node<List<T>> {
    List<Node<T>> elements;
    boolean resolved;

    public ListNode(List<Node<T>> elements) {
        this.elements = Collections.unmodifiableList(elements);
        this.resolved = elements.stream().allMatch(Node::isResolved);
    }

    @Override
    public List<Resolvable<?>> allResolvables() {
        return elements
            .stream()
            .flatMap(node -> node.allResolvables().stream())
            .collect(Collectors.toList());
    }

    @Override
    public <V> Node<List<T>> injectValue(Resolvable<V> resolvable, V value) {
        final ListNode<T> newNode = new ListNode<>(
            elements
                .stream()
                .map(node -> node.injectValue(resolvable, value))
                .collect(Collectors.toList())
        );

        return ValueNode.ifResolved(newNode);
    }

    @Override
    public List<T> getValue() {
        return elements
            .stream()
            .map(Node::getValue)
            .collect(Collectors.toList());
    }
}
