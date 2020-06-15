package haxlike.resolvers;

import haxlike.resolvers.impl.AbstractResolver;
import haxlike.resolvers.impl.ResolvableValue;

/**
 * Class for inline resolvers. This will allow you to declare resolution functions
 * with minimal overhead.
 * @param <E> environment class
 * @param <P> mandatory parameter class
 * @param <V> resolvable result class
 */
public class Resolver<E, P, V>
    extends AbstractResolver<E, P, V, ResolvableValue<P, V>> {

    private Resolver(String resolverName, ResolverFunction<E, P, V> f) {
        super(resolverName, f);
    }

    @Override
    public ResolvableValue<P, V> createResolvable(P ref) {
        return new ResolvableValue<>(this.getResolvableKey(), ref);
    }

    // --- Helper
    private static <E, P, V> Resolver<E, P, V> declareResolver(
        String resolverName,
        ResolverFunction<E, P, V> f
    ) {
        return new Resolver<>(resolverName, f);
    }

    // --- Factories
    public static <E, P, V> Resolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.Batched<E, P, V> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <E, P, V> Resolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedInOrder<E, P, V> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <E, P, V> Resolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.Single<E, P, V> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <P, V> Resolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedNoEnv<P, V> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <P, V> Resolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedInOrderNoEnv<P, V> f
    ) {
        return declareResolver(resolverName, f);
    }

    public static <P, V> Resolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.SingleNoEnv<P, V> f
    ) {
        return declareResolver(resolverName, f);
    }
}
