package haxlike.impl;

import fj.Ord;
import fj.data.TreeMap;
import haxlike.Resolvable;
import haxlike.Resolver;

final class EngineRegistry<E> {
    private final TreeMap<String, Resolver<E, ?, ?>> resolvers;

    public EngineRegistry() {
        this(TreeMap.empty(Ord.stringOrd));
    }

    private EngineRegistry(TreeMap<String, Resolver<E, ?, ?>> resolvers) {
        this.resolvers = resolvers;
    }

    public <V, R extends Resolvable<V>> EngineRegistry<E> register(
        Class<R> cls,
        Resolver<E, V, R> resolver
    ) {
        return new EngineRegistry<>(resolvers.set(cls.getName(), resolver));
    }

    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> Resolver<E, V, R> getOrThrow(
        Class<R> cls
    ) {
        return (Resolver<E, V, R>) resolvers
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
