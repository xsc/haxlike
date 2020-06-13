package haxlike.resolvers;

import fj.data.List;
import haxlike.Resolvable;

/**
 * A resolver brings together resolution logic and a resolvable class that logic
 * applies to.
 * @param <E> environment class
 * @param <R> resolvable class
 * @param <V> resolvable value class
 */
public interface ResolverDefinition<E, R extends Resolvable<V>, V> {
    /**
     * Return resolvable key this resolver will be able to resolve.
     * @return resolvable key
     */
    String getResolvableKey();

    /**
     * Create operations to resolve the given batch. Each operation
     * returns a list of pairs of the resolvable and the resulting value.
     * @param env environment to use
     * @param batch batch to resolve
     * @return list of operations
     */
    List<Operation<R, V>> createOperations(E env, List<R> batch);
}
