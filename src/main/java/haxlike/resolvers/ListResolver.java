package haxlike.resolvers;

import fj.data.List;
import haxlike.Resolvable;
import haxlike.resolvers.impl.AbstractResolver;
import haxlike.resolvers.impl.ResolvableValue;

/**
 * Class for inline resolvers. This will allow you to declare resolution functions
 * with minimal overhead.
 * @param <E> environment class
 * @param <P> mandatory parameter class
 * @param <V> resolvable result class
 */
public class ListResolver<E, P, V>
    extends AbstractResolver<E, P, List<V>, ListResolver.ToResolve<P, V>> {

    private ListResolver(
        String resolverName,
        ResolverFunction<E, P, List<V>> f
    ) {
        super(resolverName, f);
    }

    @Override
    public ToResolve<P, V> createResolvable(P ref) {
        return new ToResolve<>(this.getResolvableKey(), ref);
    }

    // --- Helper
    private static <E, P, V> ListResolver<E, P, V> declareResolver(
        String resolverName,
        ResolverFunction<E, P, List<V>> f
    ) {
        return new ListResolver<>(resolverName, f);
    }

    // --- Factories
    public static <E, P, V> ListResolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.Batched<E, P, List<V>> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <E, P, V> ListResolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedInOrder<E, P, List<V>> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <E, P, V> ListResolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.Single<E, P, List<V>> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <P, V> ListResolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedNoEnv<P, List<V>> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <P, V> ListResolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedInOrderNoEnv<P, List<V>> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <P, V> ListResolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.SingleNoEnv<P, List<V>> f
    ) {
        return declareResolver(resolverName, f);
    }

    // --- Resolvable
    public static class ToResolve<P, V>
        extends ResolvableValue<P, List<V>>
        implements Resolvable.ListResolvable<V> {

        public ToResolve(String resolvableKey, P ref) {
            super(resolvableKey, ref);
        }
    }
}
