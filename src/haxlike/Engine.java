package haxlike;

import haxlike.impl.EngineImpl;

/**
 * Interface for an engine that can be bound to an environment and
 * resolve nodes based on a series of resolvers.
 * @param <E> the environment class
 */
public interface Engine<E> {
    <V, R extends Resolvable<V>> Engine<E> register(
        Class<R> cls,
        Resolver<E, V, R> resolver
    );
    Instance build(E environment);

    interface Instance {
        <T> T resolve(Node<T> node);
    }

    @SuppressWarnings("squid:S1172")
    static <E> Engine<E> of(Class<E> environmentClass) {
        return new EngineImpl<>();
    }
}
