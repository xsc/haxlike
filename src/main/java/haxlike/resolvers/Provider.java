package haxlike.resolvers;

import fj.data.List;
import haxlike.Node;
import haxlike.resolvers.impl.AbstractResolverImpl;
import lombok.Value;

/**
 * Class for inline resolvers. This will allow you to declare resolution functions
 * with minimal overhead.
 * @param <E> environment class
 * @param <P> resolvable reference class
 * @param <V> resolvable result class
 */
public class Provider<E, V> extends AbstractResolverImpl<E, Object, V> {
    /**
     * We still need to pass an object to the operation resolver. We'll always be passing
     * the same object, without a string representation
     */
    private final RefConstant ref = new RefConstant();

    private Provider(
        String providerName,
        ResolverFunction.OperationResolver<E, Object, V> operationResolver
    ) {
        super(providerName, operationResolver);
    }

    /**
     * Return node representing this provider's resolution result.
     * @param ref reference to the value to be resolved
     * @return node representing the resolution.
     */
    public Node<V> fetch() {
        return new ResolvableValue(ref);
    }

    public V provide(E env) {
        return super.run(env, List.single(ref)).head();
    }

    public static <E, V> Provider<E, V> declare(
        String providerName,
        ResolverFunction.Provider<E, V> f
    ) {
        return new Provider<>(providerName, f.toOperationResolver());
    }

    /**
     * Class with appropriate hashCode/equals/toString semantics
     * to use as parameter for the internal {@link AbstractResolverImpl.ResolvableValue}
     * values.
     */
    @Value
    private static class RefConstant {
        private final Object object = new Object();

        @Override
        public String toString() {
            return "";
        }
    }
}
