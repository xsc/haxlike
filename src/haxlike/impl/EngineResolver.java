package haxlike.impl;

import fj.P2;
import fj.data.List;
import haxlike.Resolvable;
import haxlike.Resolver;
import haxlike.SingleResolver;
import lombok.RequiredArgsConstructor;
import lombok.Value;

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
        Resolver<E, V, R> r
    ) {
        return (env, batch) ->
            List.single(
                () -> batch.zip(r.resolveAll(env, batch)).map(Result::new)
            );
    }

    static <E, V, R extends Resolvable<V>> EngineResolver<E, V, R> from(
        SingleResolver<E, V, R> r
    ) {
        return (env, batch) ->
            batch.map(
                resolvable ->
                    () ->
                        List.single(
                            new Result<>(resolvable, r.resolve(env, resolvable))
                        )
            );
    }

    // --- Give names to things
    // We had used F0 and P2 originally, but this was not particularly readable.
    @FunctionalInterface
    interface Operation<R extends Resolvable<V>, V> {
        List<Result<R, V>> runOperation();
    }

    @Value
    @RequiredArgsConstructor
    static class Result<R extends Resolvable<V>, V> {
        R resolvable;
        V value;

        Result(P2<R, V> p) {
            this(p._1(), p._2());
        }
    }
}
