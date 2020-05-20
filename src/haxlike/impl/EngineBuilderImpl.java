package haxlike.impl;

import fj.control.parallel.Strategy;
import haxlike.Engine;
import haxlike.EngineBuilder;
import haxlike.Resolvable;
import haxlike.Resolver;
import haxlike.Resolver.Batched;
import haxlike.Resolver.Single;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class EngineBuilderImpl<E> implements EngineBuilder<E> {
    private final EngineRegistry<E> registry;
    private final Strategy<?> strategy;

    public EngineBuilderImpl() {
        this(new EngineRegistry<>(), Strategy.seqStrategy());
    }

    // --- Helper
    private <V, R extends Resolvable<V>> EngineBuilderImpl<E> register(
        Class<R> cls,
        EngineResolver<E, V, R> r
    ) {
        return new EngineBuilderImpl<>(registry.register(cls, r), strategy);
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
    public EngineBuilder<E> withStrategy(Strategy<?> s) {
        return new EngineBuilderImpl<>(registry, s);
    }

    @Override
    public Engine build(E environment) {
        return new EngineImpl<>(registry, environment, strategy);
    }
}
