package haxlike.resolvers;

import haxlike.resolvers.impl.AbstractProvider;
import haxlike.resolvers.impl.ResolvableValue;

/**
 * Class for inline providers, i.e. resolution that does not depend on user-provided
 * parameters. This will allow you to declare resolution functions with minimal overhead.
 * @param <E> environment class
 * @param <V> resolvable result class
 */
public class Provider<E, V>
    extends AbstractProvider<E, V, ResolvableValue<Object, V>> {

    private Provider(String providerName, ResolverFunction<E, Object, V> f) {
        super(providerName, f);
    }

    @Override
    public ResolvableValue<Object, V> createResolvable(Object ref) {
        return new ResolvableValue<>(this.getResolvableKey(), ref);
    }

    // --- Helper
    private static <E, V> Provider<E, V> declareProvider(
        String providerName,
        ResolverFunction<E, Object, V> f
    ) {
        return new Provider<>(providerName, f);
    }

    // --- Factories
    public static <E, V> Provider<E, V> declare(
        String providerName,
        ResolverFunction.Provider<E, V> f
    ) {
        return declareProvider(providerName, f);
    }
}
