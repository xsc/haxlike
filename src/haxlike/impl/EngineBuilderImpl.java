package haxlike.impl;

import haxlike.Engine;
import haxlike.EngineBuilder;
import haxlike.ResolutionStrategies;
import haxlike.ResolutionStrategy;
import haxlike.Resolvable;
import haxlike.Resolver;
import haxlike.SelectionStrategies;
import haxlike.SelectionStrategy;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@With(AccessLevel.PRIVATE)
public class EngineBuilderImpl<E> implements EngineBuilder<E> {
    private final EngineRegistry<E> internalRegistry;
    private final ResolutionStrategy internalResolutionStrategy;
    private final SelectionStrategy internalSelectionStrategy;

    public EngineBuilderImpl() {
        this(
            new EngineRegistry<>(),
            ResolutionStrategies.defaultStrategy(),
            SelectionStrategies.defaultStrategy()
        );
    }

    // --- Helper
    private <V, R extends Resolvable<V>> EngineBuilderImpl<E> register(
        Class<R> cls,
        EngineResolver<? super E, V, R> r
    ) {
        return this.withInternalRegistry(internalRegistry.register(cls, r));
    }

    // --- Impl
    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.Batched<? super E, V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.BatchedNoEnv<V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.BatchedInOrder<? super E, V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.BatchedInOrderNoEnv<V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.Single<? super E, V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.SingleNoEnv<V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public EngineBuilder<E> withResolutionStrategy(ResolutionStrategy s) {
        return this.withInternalResolutionStrategy(s);
    }

    @Override
    public EngineBuilder<E> withSelectionStrategy(SelectionStrategy s) {
        return this.withInternalSelectionStrategy(s);
    }

    @Override
    public Engine build(E environment) {
        return EngineImpl
            .<E>builder()
            .environment(environment)
            .registry(internalRegistry)
            .resolutionStrategy(internalResolutionStrategy)
            .selectionStrategy(internalSelectionStrategy)
            .build();
    }
}
