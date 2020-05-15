package haxlike.impl;

import haxlike.Resolvable;
import haxlike.Resolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class EngineRegistry<E> {
    private final Map<Class<? extends Resolvable<?>>, Resolver<E, ?, ?>> resolvers;

    public EngineRegistry() {
        this(new HashMap<>());
    }

    private EngineRegistry(
        Map<Class<? extends Resolvable<?>>, Resolver<E, ?, ?>> resolvers
    ) {
        this.resolvers = resolvers;
    }

    public <V, R extends Resolvable<V>> EngineRegistry<E> register(
        Class<R> cls,
        Resolver<E, V, R> resolver
    ) {
        final Map<Class<? extends Resolvable<?>>, Resolver<E, ?, ?>> copy = new HashMap<>(
            resolvers
        );
        copy.put(cls, resolver);
        return new EngineRegistry<>(copy);
    }

    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> Optional<Resolver<E, V, R>> get(
        Class<R> cls
    ) {
        return Optional
            .ofNullable(resolvers.get(cls))
            .map(r -> (Resolver<E, V, R>) r);
    }
}
