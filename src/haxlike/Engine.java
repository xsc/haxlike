package haxlike;

public interface Engine {
    <T> T resolve(Node<T> node, EngineCache cache);

    default <T> T resolve(Node<T> node) {
        return resolve(node, EngineCaches.defaultCache());
    }
}
