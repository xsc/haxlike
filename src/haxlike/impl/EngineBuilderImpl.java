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
@With
public class EngineBuilderImpl<E> implements EngineBuilder<E> {
    private static final int DEFAULT_MAX_ITERATION_COUNT = 16;

    // --- Internal Registry
    @With(AccessLevel.PRIVATE)
    private final EngineRegistry<E> registry;

    // --- Values that can be injected directly
    // These are exposed using the `@With` annotation on class-level.
    private final ResolutionStrategy resolutionStrategy;
    private final SelectionStrategy selectionStrategy;
    private final int maxIterationCount;

    public EngineBuilderImpl() {
        this(
            new EngineRegistry<>(),
            ResolutionStrategies.defaultStrategy(),
            SelectionStrategies.defaultStrategy(),
            DEFAULT_MAX_ITERATION_COUNT
        );
    }

    // --- Helper
    private <V, R extends Resolvable<V>> EngineBuilderImpl<E> register(
        Class<R> cls,
        EngineResolver<? super E, V, R> r
    ) {
        return this.withRegistry(registry.registerResolver(cls, r));
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
    public Engine build(E environment) {
        return EngineImpl
            .<E>builder()
            .environment(environment)
            .registry(registry)
            .resolutionStrategy(resolutionStrategy)
            .selectionStrategy(selectionStrategy)
            .maxIterationCount(maxIterationCount)
            .build();
    }
}
