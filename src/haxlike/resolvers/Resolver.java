package haxlike.resolvers;

import fj.F;
import fj.F2;
import fj.data.List;
import haxlike.Node;
import haxlike.relations.ParameterRelation;
import haxlike.relations.Relation;
import haxlike.resolvers.impl.AbstractResolverImpl;
import java.util.Optional;
import lombok.NonNull;

/**
 * Class for inline resolvers. This will allow you to declare resolution functions
 * with minimal overhead.
 * @param <E> environment class
 * @param <P> mandatory parameter class
 * @param <V> resolvable result class
 */
public class Resolver<E, P, V> extends AbstractResolverImpl<E, P, V> {

    Resolver(
        String resolverName,
        ResolverFunction.OperationResolver<E, P, V> operationResolver
    ) {
        super(resolverName, operationResolver);
    }

    /**
     * Return node representing this resolvers resolution result.
     * @param ref reference to the value to be resolved
     * @return node representing the resolution.
     */
    public Node<V> fetch(@NonNull P ref) {
        return new ResolvableValue(ref);
    }

    /**
     * Run this resolver using the given environment and the given batch.
     * @param env environment to use
     * @param batch batch to resolve
     * @return in-order result of the resolution
     */
    public final List<V> resolveAll(E env, List<P> batch) {
        return super.run(env, batch);
    }

    /**
     * Run this resolver using the given environment and the given value
     * @param env environment to use
     * @param value value to resolve
     * @return resolution result
     */
    public final V resolve(E env, P value) {
        return resolveAll(env, List.single(value)).head();
    }

    /**
     * Declare a relation that can be fulfilled by this resolver. Typically, this would look like
     * the following:
     *
     * <pre>
     * User.relation(Post::withAuthor, Post::getAuthorId)
     * </pre>
     * @param <T> container class
     * @param attachFunction function to be used to attach the result to the container
     * @param paramFunction function that creates the resolver parameters
     * @return a relation representing this resolver's result
     */
    public <T> Relation<T, V> relation(
        F2<T, V, T> attachFunction,
        F<T, P> paramFunction
    ) {
        return Relation.declare(
            attachFunction,
            value -> fetch(paramFunction.f(value))
        );
    }

    /**
     * Declare a relation that can be fulfilled by this resolver, allowing parameters to be passed.
     *
     * <pre>
     * Posts.relation(
     *   User::withPosts,
     *   user -> new Params(user.getId()),
     *   Params::withLimit
     * );
     * </pre>
     *
     * @param <T> type to attach this relation to
     * @param attachFunction function to be used to attach
     * @param paramFunction function that creates the resolver parameters
     * @return a relation representing this resolver's result
     */
    public <T, R> ParameterRelation<T, R, V> relation(
        F2<T, V, T> attachFunction,
        F<T, P> paramFunction,
        F2<P, R, P> adaptFunction
    ) {
        return Relation.declare(
            attachFunction,
            (value, additionalParams) -> {
                final P initialParams = paramFunction.f(value);
                final P fullParams = Optional
                    .ofNullable(additionalParams)
                    .map(values -> adaptFunction.f(initialParams, values))
                    .orElse(initialParams);
                return fetch(fullParams);
            },
            null
        );
    }

    // --- Factories
    public static <E, P, V> Resolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.Batched<E, P, V> f
    ) {
        return new Resolver<>(resolverName, f.toOperationResolver());
    }

    public static <E, P, V> Resolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedInOrder<E, P, V> f
    ) {
        return new Resolver<>(resolverName, f.toOperationResolver());
    }

    public static <E, P, V> Resolver<E, P, V> declare(
        String resolverName,
        ResolverFunction.Single<E, P, V> f
    ) {
        return new Resolver<>(resolverName, f.toOperationResolver());
    }

    public static <P, V> Resolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedNoEnv<P, V> f
    ) {
        return new Resolver<>(resolverName, f.toOperationResolver());
    }

    public static <P, V> Resolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.BatchedInOrderNoEnv<P, V> f
    ) {
        return new Resolver<>(resolverName, f.toOperationResolver());
    }

    public static <P, V> Resolver<Object, P, V> declare(
        String resolverName,
        ResolverFunction.SingleNoEnv<P, V> f
    ) {
        return new Resolver<>(resolverName, f.toOperationResolver());
    }
}
