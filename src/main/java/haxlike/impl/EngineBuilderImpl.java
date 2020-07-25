package haxlike.impl;

import haxlike.Engine;
import haxlike.EngineBuilder;
import haxlike.ResolutionStrategies;
import haxlike.ResolutionStrategy;
import haxlike.Resolvable;
import haxlike.SelectionStrategies;
import haxlike.SelectionStrategy;
import haxlike.resolvers.ResolverDefinition;
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

    // --- Internal Data
    @With(AccessLevel.PRIVATE)
    private final EngineRegistry<E> registry;

    @With(AccessLevel.PRIVATE)
    private final EngineLogger logger;

    // --- Values that can be injected directly
    // These are exposed using the `@With` annotation on class-level.
    private final ResolutionStrategy resolutionStrategy;
    private final SelectionStrategy selectionStrategy;
    private final int maxIterationCount;

    public EngineBuilderImpl() {
        this(
            new EngineRegistry<>(),
            new EngineLogger.NoOp(),
            ResolutionStrategies.defaultStrategy(),
            SelectionStrategies.defaultStrategy(),
            DEFAULT_MAX_ITERATION_COUNT
        );
    }

    // --- Impl
    @Override
    public <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        ResolverDefinition<? super E, R, V> resolver
    ) {
        return this.withRegistry(registry.registerResolver(resolver));
    }

    @Override
    public EngineBuilder<E> withTraceLogging() {
        return this.withLogger(new EngineLogger.Slf4j());
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
            .logger(logger)
            .build();
    }
}
