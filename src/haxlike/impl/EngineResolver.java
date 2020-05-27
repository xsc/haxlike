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
    List<Operation<R, V>> createOperations(E env, List<R> batch);

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.Batched<E, V, R> r
    ) {
        return (env, batch) -> runBatchedResolver(r, env, batch);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        Resolver.BatchedInOrder<E, V, R> r
    ) {
        final Resolver.Batched<E, V, R> br = (env, batch) ->
            Results.zip(batch, r.resolveAll(env, batch));
        return from(br);
    }

    @SuppressWarnings("squid:S1172")
    static <E, V, R extends Resolvable<V> & Resolver.Batched<E, V, R>> EngineResolver<E, V, R> from(
        Class<R> cls
    ) {
        return (env, batch) -> runBatchedResolver(batch.head(), env, batch);
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> fromSingle(
        Resolver.Single<E, V, R> r
    ) {
        return (env, batch) -> runSingleResolver(r, env, batch);
    }

    @SuppressWarnings("squid:S1172")
    static <E, V, R extends Resolvable<V> & Resolver.Single<E, V, R>> EngineResolver<E, V, R> fromSingle(
        Class<R> cls
    ) {
        return (env, batch) -> runSingleResolver(batch.head(), env, batch);
    }

    // --- Helpers
    static <E, V, R extends Resolvable<V>> List<Operation<R, V>> runBatchedResolver(
        Resolver.Batched<E, V, R> r,
        E env,
        List<R> batch
    ) {
        return List.single(
            new Operation<>(batch, () -> r.resolveAll(env, batch))
        );
    }

    static <E, V, R extends Resolvable<V>> List<Operation<R, V>> runSingleResolver(
        Resolver.Single<E, V, R> r,
        E env,
        List<R> batch
    ) {
        return batch.map(
            resolvable ->
                new Operation<>(
                    List.single(resolvable),
                    () -> Results.single(resolvable, r.resolve(env, resolvable))
                )
        );
    }
}
