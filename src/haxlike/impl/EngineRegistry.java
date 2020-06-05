package haxlike.impl;

import fj.Ord;
import fj.data.TreeMap;
import haxlike.Resolvable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.With;

@RequiredArgsConstructor
@With(AccessLevel.PRIVATE)
final class EngineRegistry<E> {
    // --- Data
    private final TreeMap<String, EngineResolver<? super E, ?, ?>> resolvers;

    public EngineRegistry() {
        this(TreeMap.empty(Ord.stringOrd));
    }

    // --- Resolvers
    public <V, R extends Resolvable<V>> EngineRegistry<E> registerResolver(
        Class<R> cls,
        EngineResolver<? super E, V, R> resolver
    ) {
        return this.withResolvers(resolvers.set(cls.getName(), resolver));
    }

    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> EngineResolver<E, V, R> getResolverOrThrow(
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
