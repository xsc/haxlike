package haxlike;

import haxlike.impl.EngineBuilderImpl;
import haxlike.projections.Projection;

public interface Engine {
    <T> T resolve(Node<T> node, EngineCache cache);

    default <T> T resolve(Node<T> node) {
        return resolve(node, EngineCaches.defaultCache());
    }

    default <T> T resolve(Node<T> node, Projection<T> projection) {
        return resolve(projection.project(node));
    }

    /**
     * Create a fresh {@link EngineBuilder} for the given environment class.
     * @param <E> class of the environment
     * @param environmentClass class of the environment
     * @return a fresh
     */
    static <E> EngineBuilder<E> builder() {
        return new EngineBuilderImpl<>();
    }
}
