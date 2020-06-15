package haxlike.resolvers.impl;

import fj.data.List;
import haxlike.resolvers.ResolverFunction;
import lombok.Value;

/**
 * Class for inline resolvers. This will allow you to declare resolution functions
 * with minimal overhead.
 * @param <E> environment class
 * @param <P> resolvable reference class
 * @param <V> resolvable result class
 * @param <R> internal resolvable class
 */
public abstract class AbstractProvider<E, V, R extends ResolvableValue<Object, V>>
    extends AbstractResolverDefinition<E, Object, V, R> {
    /**
     * We still need to pass an object to the operation resolver. We'll always be passing
     * the same object, without a string representation
     */
    private final RefConstant ref = new RefConstant();

    protected AbstractProvider(
        String providerName,
        ResolverFunction<E, Object, V> f
    ) {
        super(providerName, f.toOperationResolver());
    }

    /**
     * Return node representing this provider's resolution result.
     * @param ref reference to the value to be resolved
     * @return node representing the resolution.
     */
    public R fetch() {
        return this.createResolvable(ref);
    }

    /**
     * Run the provider function with the given environment
     * @param env environment to use
     * @return provided value
     */
    public V provide(E env) {
        return super.run(env, List.single(ref)).head();
    }

    /**
     * Class with appropriate hashCode/equals/toString semantics
     * to use as parameter for the internal {@link AbstractResolverDefinition.ResolvableValue}
     * values.
     */
    @Value
    public static class RefConstant {
        private final Object object = new Object();

        @Override
        public String toString() {
            return "";
        }
    }
}
