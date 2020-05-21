package haxlike.nodes.decorators;

import haxlike.Node;

public class NamedNodeDecorator<T> extends NodeDecorator<T> {
    private final String name;

    public NamedNodeDecorator(Node<T> inner, String name) {
        super(inner);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
