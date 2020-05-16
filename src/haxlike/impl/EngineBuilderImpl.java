package haxlike.impl;

import fj.control.parallel.Strategy;
import haxlike.Engine;
import haxlike.EngineBuilder;
import haxlike.Resolvable;
import haxlike.Resolver;
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

    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver<E, V, R> resolver
    ) {
        return new EngineBuilderImpl<>(
            registry.register(cls, resolver),
            strategy
        );
    }

    @Override
    public EngineBuilder<E> withStrategy(Strategy<?> s) {
        return new EngineBuilderImpl<>();
    }

    @Override
    public Engine build(E environment) {
        return new EngineImpl<>(registry, environment, strategy);
    }
}
