package haxlike.resolvers.impl;

import fj.F;
import fj.F2;
import fj.data.List;
import haxlike.relations.ParameterRelation;
import haxlike.relations.Relation;
import haxlike.resolvers.ResolverFunction;
import lombok.NonNull;

/**
 * Class for inline resolvers. This will allow you to declare resolution functions
 * with minimal overhead.
 * @param <E> environment class
 * @param <P> mandatory parameter class
 * @param <V> resolvable result class
 * @param <R> internal resolvable class
 */

public abstract class AbstractResolver<E, P, V, R extends ResolvableValue<P, V>>
    extends AbstractResolverDefinition<E, P, V, R> {

    protected AbstractResolver(
        String resolverName,
        ResolverFunction<E, P, V> f
    ) {
        super(resolverName, f.toOperationResolver());
    }

    /**
     * Return node representing this resolvers resolution result.
     * @param ref reference to the value to be resolved
     * @return node representing the resolution.
     */
    public R fetch(@NonNull P ref) {
        return this.createResolvable(ref);
    }

    /**
     * Run this resolver using the given environment and the given batch.
     * @param env environment to use
     * @param batch batch to resolve
     * @return in-order result of the resolution
     */
    public final List<V> resolveAll(E env, @NonNull List<P> batch) {
        return super.run(env, batch);
    }

    /**
     * Run this resolver using the given environment and the given value
     * @param env environment to use
     * @param value value to resolve
     * @return resolution result
     */
    public final V resolve(E env, @NonNull P value) {
        return resolveAll(env, List.single(value)).head();
    }

    /**
     * Declare a (parameterisable) relation that can be fulfilled by this resolver.
     *
     * <pre>
     * User.relation(Post::withAuthor, Post::getAuthorId);
     * </pre>
     *
     * It is highly recommended to use {@link lombok.Value} and {@link lombok.With} on
     * entities containing relations.
     *
     * @param <T> type to attach this relation to
     * @param attachFunction function to be used to attach
     * @param paramFunction function that creates the parameter passed to {@link Resolver#fetch(Object)}
     * @return a relation representing this resolver's result
     */
    public <T> ParameterRelation<T, P, V> relation(
        F2<T, V, T> attachFunction,
        F<T, P> paramFunction
    ) {
        return Relation.declareWithParameters(
            attachFunction,
            this::fetch,
            paramFunction
        );
    }
}
