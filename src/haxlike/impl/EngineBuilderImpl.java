package haxlike.impl;

import haxlike.Engine;
import haxlike.EngineBuilder;
import haxlike.ResolutionStrategies;
import haxlike.ResolutionStrategy;
import haxlike.Resolvable;
import haxlike.Resolver;
import haxlike.Resolver.Batched;
import haxlike.Resolver.Single;
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
        EngineResolver<E, V, R> r
    ) {
        return this.withInternalRegistry(internalRegistry.register(cls, r));
    }

    // --- Impl
    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.Batched<E, V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.BatchedInOrder<E, V, R> resolver
    ) {
        return register(cls, EngineResolver.from(resolver));
    }

    @Override
    public <V, R extends Resolvable<V> & Batched<E, V, R>> EngineBuilder<E> withResolvable(
        Class<R> cls
    ) {
        return register(cls, EngineResolver.from(cls));
    }

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.Single<E, V, R> resolver
    ) {
        return register(cls, EngineResolver.fromSingle(resolver));
    }

    @Override
    public <V, R extends Resolvable<V> & Single<E, V, R>> EngineBuilder<E> withSingleResolvable(
        Class<R> cls
    ) {
        return register(cls, EngineResolver.fromSingle(cls));
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
