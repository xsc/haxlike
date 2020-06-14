package haxlike.resolvers.impl;

import fj.data.HashMap;
import fj.data.List;
import haxlike.Resolvable;
import haxlike.resolvers.Operation;
import haxlike.resolvers.ResolverDefinition;
import haxlike.resolvers.ResolverFunction;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Class for inline resolvers. This will allow you to declare resolution
 * functions with minimal overhead.
 *
 * @param <E> environment class
 * @param <P> resolvable reference class
 * @param <V> resolvable result class
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractResolverImpl<E, P, V>
    implements
        ResolverDefinition<E, AbstractResolverImpl<E, P, V>.ResolvableValue, V> {
    private final String resolverName;
    private final ResolverFunction.OperationResolver<E, P, V> operationResolver;

    @Override
    public String getResolvableKey() {
        return resolverName;
    }

    @Override
    public final List<Operation<ResolvableValue, V>> createOperations(
        E env,
        List<ResolvableValue> batch
    ) {
        return operationResolver
            .createOperations(env, batch.map(ResolvableValue::getRef))
            .map(op -> () -> op.runOperation().mapKeys(ResolvableValue::new));
    }

    /**
     * Run this resolver using the given environment and the given batch.
     * @param env environment to use
     * @param batch batch to resolve
     * @return in-order result of the resolution
     */
    protected final List<V> run(E env, List<P> batch) {
        final List<Operation<P, V>> ops = operationResolver.createOperations(
            env,
            batch
        );
        final HashMap<P, V> results = HashMap.hashMap();
        ops.forEach(op -> op.runOperation().into(results));
        return batch.map(p -> results.get(p).some());
    }

    /**
     * Internal class to represent resolvables that this resolver can handle.
     */
    @Value
    public class ResolvableValue implements Resolvable<V> {
        private final String resolvableKey = resolverName;

        @NonNull
        P ref;

        @Override
        public String toString() {
            return (resolverName + "(" + ref + ")");
        }
    }
}
