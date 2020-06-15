package haxlike.resolvers;

import fj.data.List;
import haxlike.resolvers.impl.AbstractProvider;
import haxlike.resolvers.impl.ResolvableValue;
import haxlike.traits.ListResolvable;

/**
 * Class for inline resolvers. This will allow you to declare resolution
 * functions with minimal overhead.
 *
 * @param <E> environment class
 * @param <P> resolvable reference class
 * @param <V> resolvable result class
 */
public class ListProvider<E, V>
    extends AbstractProvider<E, List<V>, ListProvider.ToResolve<V>> {

    private ListProvider(
        String resolverName,
        ResolverFunction<E, Object, List<V>> f
    ) {
        super(resolverName, f);
    }

    @Override
    public ToResolve<V> createResolvable(Object ref) {
        return new ToResolve<>(this.getResolvableKey(), ref);
    }

    // --- Helper
    private static <E, V> ListProvider<E, V> declareProvider(
        String resolverName,
        ResolverFunction<E, Object, List<V>> f
    ) {
        return new ListProvider<>(resolverName, f);
    }

    // --- Factories
    public static <E, V> ListProvider<E, V> declare(
        String providerName,
        ResolverFunction.Provider<E, List<V>> f
    ) {
        return declareProvider(providerName, f);
    }

    // --- Resolvable
    public static class ToResolve<V>
        extends ResolvableValue<Object, List<V>>
        implements ListResolvable<V> {

        public ToResolve(String resolvableKey, Object ref) {
            super(resolvableKey, ref);
        }
    }
}
