package haxlike.impl;

import fj.Ord;
import fj.data.TreeMap;
import haxlike.Resolvable;
import haxlike.resolvers.ResolverDefinition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.With;

@RequiredArgsConstructor
@With(AccessLevel.PRIVATE)
final class EngineRegistry<E> {
    // --- Data
    private final TreeMap<String, ResolverDefinition<? super E, ?, ?>> resolvers;

    public EngineRegistry() {
        this(TreeMap.empty(Ord.stringOrd));
    }

    // --- Resolvers
    public <R extends Resolvable<V>, V> EngineRegistry<E> registerResolver(
        ResolverDefinition<? super E, R, V> resolver
    ) {
        return this.withResolvers(
                resolvers.set(resolver.getResolvableKey(), resolver)
            );
    }

    @SuppressWarnings("unchecked")
    public <R extends Resolvable<V>, V> ResolverDefinition<E, R, V> getResolverOrThrow(
        R proto
    ) {
        return (ResolverDefinition<E, R, V>) resolvers
            .get(proto.getResolvableKey())
            .orSome(
                () -> {
                    throw new IllegalStateException(
                        "No resolver for key: " + proto.getResolvableKey()
                    );
                }
            );
    }
}
