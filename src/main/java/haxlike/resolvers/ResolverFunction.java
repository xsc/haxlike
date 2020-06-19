package haxlike.resolvers;

import fj.data.List;
import haxlike.Resolvable;
import haxlike.resolvers.impl.ResolverDefinitionImpl;

/**
 * Function representing resolution logic (independent of {@link haxlike.Resolvable}).
 * @param <E>
 * @param <R>
 * @param <V>
 */
public interface ResolverFunction<E, R, V> {
    OperationResolver<E, R, V> toOperationResolver();

    /**
     * Utility interface for operation creation
     * @param <E> environment class
     * @param <R> resolvable class
     * @param <V> resolvable value class
     */
    @FunctionalInterface
    public static interface OperationResolver<E, R, V>
        extends ResolverFunction<E, R, V> {
        List<Operation<R, V>> createOperations(E env, List<R> batch);

        @Override
        default OperationResolver<E, R, V> toOperationResolver() {
            return this;
        }
    }

    /**
     * A batched resolver is a function that will use a supplied environment
     * to return a list of results based on a list of values-to-resolve.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface Provider<E, V>
        extends ResolverFunction<E, Object, V> {
        V provide(E environment);

        @Override
        default OperationResolver<E, Object, V> toOperationResolver() {
            return (env, batch) ->
                List.single(
                    () -> {
                        final V result = this.provide(env);
                        return Results.map(batch, r -> result);
                    }
                );
        }
    }

    /**
     * A batched resolver is a function that will use a supplied environment
     * to return a list of results based on a list of values-to-resolve.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface ProviderNoEnv<V>
        extends ResolverFunction<Object, Object, V> {
        V provide();

        @Override
        default OperationResolver<Object, Object, V> toOperationResolver() {
            return (env, batch) ->
                List.single(
                    () -> {
                        final V result = this.provide();
                        return Results.map(batch, r -> result);
                    }
                );
        }
    }

    /**
     * A batched resolver is a function that will use a supplied environment
     * to return a list of results based on a list of values-to-resolve.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface Batched<E, R, V> extends ResolverFunction<E, R, V> {
        Results<R, V> resolveAll(E environment, List<R> resolvables);

        @Override
        default OperationResolver<E, R, V> toOperationResolver() {
            return (env, batch) ->
                List.single(() -> this.resolveAll(env, batch));
        }
    }

    /**
     * Like {@link Batched} but does not take an environment.
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface BatchedNoEnv<R, V>
        extends ResolverFunction<Object, R, V> {
        Results<R, V> resolveAll(List<R> resolvables);

        @Override
        default OperationResolver<Object, R, V> toOperationResolver() {
            return (env, batch) -> List.single(() -> this.resolveAll(batch));
        }
    }

    /**
     * A single resolver is a function that will use a supplied environment
     * to return a single result based on a value-to-resolve.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface Single<E, R, V> extends ResolverFunction<E, R, V> {
        V resolve(E env, R resolvable);

        @Override
        default OperationResolver<E, R, V> toOperationResolver() {
            return (env, batch) ->
                batch.map(r -> () -> Results.single(r, this.resolve(env, r)));
        }
    }

    /**
     * Like {@link Single} but does not take an environment.
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface SingleNoEnv<R, V>
        extends ResolverFunction<Object, R, V> {
        V resolve(R resolvable);

        @Override
        default OperationResolver<Object, R, V> toOperationResolver() {
            return (env, batch) ->
                batch.map(r -> () -> Results.single(r, this.resolve(r)));
        }
    }

    /**
     * A batched resolver is a function that will use a supplied environment
     * to return a list of results based on a list of values-to-resolve. This
     * is a special version that assumes that results are returned in an
     * order matching the input resolvables.
     * @param <E> environment class
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface BatchedInOrder<E, R, V>
        extends ResolverFunction<E, R, V> {
        List<V> resolveAll(E environment, List<R> resolvables);

        @Override
        default OperationResolver<E, R, V> toOperationResolver() {
            return (env, batch) ->
                List.single(
                    () -> Results.zip(batch, this.resolveAll(env, batch))
                );
        }
    }

    /**
     * Like {@link BatchedInOrder} but does not take an environment.
     * @param <V> return value of a single resolvable
     * @param <R> resolvable class
     */
    @FunctionalInterface
    public static interface BatchedInOrderNoEnv<R, V>
        extends ResolverFunction<Object, R, V> {
        List<V> resolveAll(List<R> resolvables);

        @Override
        default OperationResolver<Object, R, V> toOperationResolver() {
            return (env, batch) ->
                List.single(() -> Results.zip(batch, this.resolveAll(batch)));
        }
    }

    /**
     * Convert the given resolver function to a {@link ResolverDefinition}.
     * @param <E> environment class
     * @param <R> resolvable class
     * @param <V> resolvable result class
     * @param cls resolvable class
     * @param f resolver function
     * @return resolver definition
     */
    public static <E, R extends Resolvable<V>, V> ResolverDefinition<E, R, V> toResolverDefinition(
        Class<R> cls,
        ResolverFunction<E, R, V> f
    ) {
        return new ResolverDefinitionImpl<>(cls, f);
    }
}
