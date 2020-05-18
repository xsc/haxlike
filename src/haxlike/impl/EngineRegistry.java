package haxlike.impl;

import fj.Ord;
import fj.data.TreeMap;
import haxlike.Resolvable;

final class EngineRegistry<E> {
    // --- Data
    private final TreeMap<String, EngineResolver<E, ?, ?>> resolvers;

    public EngineRegistry() {
        this(TreeMap.empty(Ord.stringOrd));
    }

    private EngineRegistry(TreeMap<String, EngineResolver<E, ?, ?>> resolvers) {
        this.resolvers = resolvers;
    }

    // --- Register
    public <V, R extends Resolvable<V>> EngineRegistry<E> register(
        Class<R> cls,
        EngineResolver<E, V, R> resolver
    ) {
        return new EngineRegistry<>(resolvers.set(cls.getName(), resolver));
    }

    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> EngineResolver<E, V, R> getOrThrow(
        Class<R> cls
    ) {
        return (EngineResolver<E, V, R>) resolvers
            .get(cls.getName())
            .orSome(
                () -> {
                    throw new IllegalStateException(
                        "No resolver for class: " + cls.getName()
                    );
                }
            );
    }
}
