package haxlike;

import fj.control.parallel.Strategy;
import haxlike.impl.EngineBuilderImpl;
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
     * Register a new resolver (batched)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.Batched<E, V, R> resolver
    );

    /**
     * Register a new resolver (batched, returning a list of in-order results)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.BatchedInOrder<E, V, R> resolver
    );

    /**
     * Register a new resolver (single)
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    <V, R extends Resolvable<V>> EngineBuilder<E> withResolver(
        Class<R> cls,
        Resolver.Single<E, V, R> resolver
    );

    /**
     * Register a new resolver (batched) for a resolvable class directly implementing its
     * own resolver.
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    <V, R extends Resolvable<V> & Resolver.Batched<E, V, R>> EngineBuilder<E> withResolvable(
        Class<R> cls
    );

    /**
     * Register a new resolver (single) for a resolvable class directly implementing its
     * own resolver.
     * @param <V> value class
     * @param <R> resolvable class producing the value
     * @param cls resolvable class to register
     * @param resolver resolver to register
     * @return a new EngineBuilder that has the resolver registered
     */
    <V, R extends Resolvable<V> & Resolver.Single<E, V, R>> EngineBuilder<E> withSingleResolvable(
        Class<R> cls
    );

    /**
     * Set the resolution {@link Strategy} in regards to parallelism. By default,
     * resolution happens synchronously using {@link Strategy#seqStrategy()}.
     * @param s resolution strategy
     * @return a new EngineBuilder with the resolution strategy applied
     */
    EngineBuilder<E> withStrategy(Strategy<?> s);

    /**
     * Build an engine bound to the given environment.
     * @param environment the environment that will be available to resolvers.
     * @return an Engine instance
     */
    Engine build(E environment);

    /**
     * Shorthand for {@link EngineBuilder#withStrategy()} when it's desired to
     * use parallel execution using the given executor service.
     * @param s the ExecutorService to use for resolution
     * @return a new EngineBuilder with the resolution strategy applied
     */
    default EngineBuilder<E> withExecutorService(ExecutorService s) {
        return withStrategy(Strategy.executorStrategy(s));
    }

    /**
     * Shorthand for {@link EngineBuilder#withStrategy()} when it's desired to
     * use parallel execution using {@link ForkJoinPool#commonPool()}</code>.
     * @return
     */
    default EngineBuilder<E> withCommonForkJoinPool() {
        return withExecutorService(ForkJoinPool.commonPool());
    }

    /**
     * Create a new EngineBuilder for the given environment class
     * @param <E> environment class
     * @param environmentClass environment class
     * @return a fresh EngineBuilder
     */
    @SuppressWarnings("squid:S1172")
    static <E> EngineBuilder<E> of(Class<E> environmentClass) {
        return new EngineBuilderImpl<>();
    }
}
