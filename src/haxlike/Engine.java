package haxlike;

@FunctionalInterface
public interface Engine {
    <T> T resolve(Node<T> node);
}
