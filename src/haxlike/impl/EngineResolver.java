package haxlike.impl;

import fj.data.List;
import haxlike.Operation;
import haxlike.Resolvable;
import haxlike.Resolver;
import haxlike.Results;

/**
 * This is a helper interface to represent the single operations necessary
 * to perform the resolution of a single batch:
 * - Batched resolution will result in one operation to execute, returning a
 *   list of results.
 * - One-by-one resolution will result in multiple operations, all returning
 *   a single result.
 * @param <E> environment class
 * @param <V> value class
 * @param <R> resolvable class
 */
@FunctionalInterface
interface EngineResolver<E, V, R extends Resolvable<V>> {
    /**
     * Create operations to resolve the given batch. Each operation
     * returns a list of pairs of the resolvable and the resulting value.
     * @param env
     * @param batch
     * @return
     */
    List<Operation> createOperations(E env, List<R> batch);

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.Batched<? super E, V, R> r
    ) {
        return (env, batch) -> runBatchedResolver(r, env, batch);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.BatchedNoEnv<V, R> r
    ) {
        final Resolver.Batched<E, V, R> br = (env, batch) ->
            r.resolveAll(batch);
        return from(br);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.BatchedInOrder<? super E, V, R> r
    ) {
        final Resolver.Batched<E, V, R> br = (env, batch) ->
            Results.zip(batch, r.resolveAll(env, batch));
        return from(br);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.BatchedInOrderNoEnv<V, R> r
    ) {
        final Resolver.Batched<E, V, R> br = (env, batch) ->
            Results.zip(batch, r.resolveAll(batch));
        return from(br);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.Single<? super E, V, R> r
    ) {
        return (env, batch) -> runSingleResolver(r, env, batch);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.SingleNoEnv<V, R> r
    ) {
        final Resolver.Single<E, V, R> sr = (env, resolvable) ->
            r.resolve(resolvable);
        return from(sr);
    }

    // --- Helpers
    private static <E, V, R extends Resolvable<V>> List<Operation> runBatchedResolver(
        Resolver.Batched<? super E, V, R> r,
        E env,
        List<R> batch
    ) {
        return List.single(() -> r.resolveAll(env, batch));
    }

    private static <E, V, R extends Resolvable<V>> List<Operation> runSingleResolver(
        Resolver.Single<? super E, V, R> r,
        E env,
        List<R> batch
    ) {
        return batch.map(
            resolvable ->
                () -> Results.single(resolvable, r.resolve(env, resolvable))
        );
    }
}
