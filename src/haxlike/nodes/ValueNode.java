package haxlike.nodes;

import haxlike.Node;
import lombok.Value;

@Value
public class ValueNode<T> implements Node.WithValue<T> {
    T value;
}
