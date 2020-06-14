package haxlike;

import haxlike.resolvers.ResolverDefinition;
import haxlike.resolvers.ResolverFunction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * An engine builder for customisation of resolution strategy and
 * parallelism. Implementations of this class are supposed to be
 * immutable, so make sure to use the fluent API.
 *
 * @param <E> the environment class
 */
public interface EngineBuilder<E> {
    /**
     * Register a new resolver definition
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param resolver resolver definition to register
     * @return a new EngineBuilder that has the resolver definition registered
     */
    <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        ResolverDefinition<? super E, R, V> resolver
    );

    /**
     * Set the {@link SelectionStrategy} to steer order of resolution.
     * By default, all available batches are selected using
     * {@link SelectionStrategies#defaultStrategy()}.
     * @param s selection strategy
     * @return a new EngineBuilder with the resolution strategy applied
     */
    EngineBuilder<E> withSelectionStrategy(SelectionStrategy s);

    /**
     * Set the {@link ResolutionStrategy} in regards to parallelism.
     * By default, resolution happens synchronously using
     * {@link ResolutionStrategies#defaultStrategy()}.
     * @param s resolution strategy
     * @return a new EngineBuilder with the resolution strategy applied
     */
    EngineBuilder<E> withResolutionStrategy(ResolutionStrategy s);

    /**
     * Set the maximum number of iteration. An engine will throw an
     * {@link IllegalStateException} if the value is exceeded.
     * @param depth maximum resolution depth
     * @return a new EngineBuilder with the maximum resolution depth applied
     */
    EngineBuilder<E> withMaxIterationCount(int depth);

    /**
     * Build an engine bound to the given environment.
     * @param environment the environment that will be available to resolvers.
     * @return an Engine instance
     */
    Engine build(E environment);

    /**
     * Shorthand for {@link EngineBuilder#withResolutionStrategy()} when it's desired to
     * use parallel execution using the given executor service.
     * @param s the ExecutorService to use for resolution
     * @return a new EngineBuilder with the resolution strategy applied
     */
    default EngineBuilder<E> withExecutorService(ExecutorService s) {
        return withResolutionStrategy(
            ResolutionStrategies.executorServiceStrategy(s)
        );
    }

    /**
     * Shorthand for {@link EngineBuilder#withResolutionStrategy()} when it's desired to
     * use parallel execution using {@link ForkJoinPool#commonPool()}</code>.
     * @return
     */
    default EngineBuilder<E> withCommonForkJoinPool() {
        return withExecutorService(ForkJoinPool.commonPool());
    }

    /**
     * Register a new resolver function
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver function to register
     * @return a new EngineBuilder that has the resolver functino registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolverFunction(
        Class<R> cls,
        ResolverFunction<? super E, R, V> resolver
    ) {
        return this.withResolver(
                ResolverFunction.toResolverDefinition(cls, resolver)
            );
    }

    /**
     * Register a new resolver (batched)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        ResolverFunction.Batched<? super E, R, V> resolver
    ) {
        return this.withResolverFunction(cls, resolver);
    }

    /**
     * Register a new resolver (batched)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        ResolverFunction.BatchedNoEnv<R, V> resolver
    ) {
        return this.withResolverFunction(cls, resolver);
    }

    /**
     * Register a new resolver (batched, returning a list of in-order results)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        ResolverFunction.BatchedInOrder<? super E, R, V> resolver
    ) {
        return this.withResolverFunction(cls, resolver);
    }

    /**
     * Register a new resolver (single)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        ResolverFunction.BatchedInOrderNoEnv<R, V> resolver
    ) {
        return this.withResolverFunction(cls, resolver);
    }

    /**
     * Register a new resolver (single)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        ResolverFunction.Single<? super E, R, V> resolver
    ) {
        return this.withResolverFunction(cls, resolver);
    }

    /**
     * Register a new resolver (single)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    default <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        ResolverFunction.SingleNoEnv<R, V> resolver
    ) {
        return this.withResolverFunction(cls, resolver);
    }
}
