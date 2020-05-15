package haxlike.nodes;

import haxlike.Node;
import haxlike.Resolvable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;

@Value
public class ListNode<T> implements Node.WithoutValue<List<T>> {
    List<Node<T>> elements;

    @Override
    public List<Resolvable<?, ?>> allResolvables() {
        return elements
            .stream()
            .flatMap(node -> node.allResolvables().stream())
            .collect(Collectors.toList());
    }

    @Override
    public <V, R extends Resolvable<V, R>> Node<List<T>> injectValue(
        R resolvable,
        V value
    ) {
        final List<Node<T>> injected = elements
            .stream()
            .map(node -> node.injectValue(resolvable, value))
            .collect(Collectors.toList());

        if (injected.stream().allMatch(Node::hasValue)) {
            return new ValueNode<>(
                injected
                    .stream()
                    .map(Node::getValue)
                    .collect(Collectors.toList())
            );
        }

        return new ListNode<>(injected);
    }
}
