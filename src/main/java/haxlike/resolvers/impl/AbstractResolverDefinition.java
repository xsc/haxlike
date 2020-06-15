package haxlike.resolvers.impl;

import fj.data.HashMap;
import fj.data.List;
import haxlike.resolvers.Operation;
import haxlike.resolvers.ResolverDefinition;
import haxlike.resolvers.ResolverFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Class for inline resolvers. This will allow you to declare resolution
 * functions with minimal overhead.
 *
 * @param <E> environment class
 * @param <P> resolvable reference class
 * @param <V> resolvable result class
 * @param <R> internal resolvable class
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractResolverDefinition<E, P, V, R extends ResolvableValue<P, V>>
    implements ResolverDefinition<E, R, V> {
    @NonNull
    @Getter
    private final String resolvableKey;

    @NonNull
    private final ResolverFunction.OperationResolver<E, P, V> operationResolver;

    @Override
    public final List<Operation<R, V>> createOperations(E env, List<R> batch) {
        return operationResolver
            .createOperations(env, batch.map(ResolvableValue::getRef))
            .map(op -> () -> op.runOperation().mapKeys(this::createResolvable));
    }

    /**
     * Create internal resolvable for the given reference
     * @param ref reference value
     * @return internal resolvable
     */
    public abstract R createResolvable(P ref);

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
}
