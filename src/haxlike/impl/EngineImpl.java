package haxlike.impl;

import haxlike.Engine;
import haxlike.Resolvable;
import haxlike.Resolver;

public class EngineImpl<E> implements Engine<E> {
    private final EngineRegistry<E> registry;

    public EngineImpl() {
        this(new EngineRegistry<>());
    }

    private EngineImpl(EngineRegistry<E> registry) {
        this.registry = registry;
    }

    @Override
    public <V, R extends Resolvable<V>> Engine<E> register(
        Class<R> cls,
        Resolver<E, V, R> resolver
    ) {
        return new EngineImpl<>(registry.register(cls, resolver));
    }

    @Override
    public Instance build(E environment) {
        return new EngineInstance<>(registry, environment);
    }
}
