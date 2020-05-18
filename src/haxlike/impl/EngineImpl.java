package haxlike.impl;

import fj.Ord;
import fj.control.parallel.Strategy;
import fj.data.List;
import haxlike.Engine;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.impl.EngineResolver.Operation;
import haxlike.impl.EngineResolver.Result;
import lombok.Value;

@Value
class EngineImpl<E> implements Engine {
    EngineRegistry<E> registry;
    E environment;
    Strategy<?> strategy;

    @Override
    public <T> T resolve(Node<T> node) {
        Node<T> n = node;
        while (!n.isResolved()) {
            n = resolveNext(n);
        }
        return n.getValue();
    }

    /**
     * Resolve the next available batches of resolvables
     * @param <T> target class
     * @param node node to resolve
     * @return a node with elements resolved
     */
    private <T, V, R extends Resolvable<V>> Node<T> resolveNext(Node<T> node) {
        final List<List<R>> batches = selectNextBatches(node);
        final List<Operation<R, V>> operations = batches.bind(
            this::createOperations
        );

        return runOperations(operations)
            .foldLeft(EngineImpl::injectOperationResult, node);
    }

    @SuppressWarnings("unchecked")
    private <V, R extends Resolvable<V>> List<Result<R, V>> runOperations(
        List<Operation<R, V>> operations
    ) {
        return ((Strategy<List<Result<R, V>>>) strategy).parMap1(
                Operation::runOperation,
                operations
            )
            .bind(l -> l);
    }

    /**
     * Create a stream of batches to resolve, based on the desired parallelism.
     * @param <T>  target class
     * @param <V>  resolvable value class
     * @param <R>  resolvable class
     * @param node node to select batches for
     * @return stream of batches
     */
    @SuppressWarnings("unchecked")
    private <T, V, R extends Resolvable<V>> List<List<R>> selectNextBatches(
        Node<T> node
    ) {
        return ((List<R>) node.getResolvables()).groupBy(
                r -> r.getClass().getName(),
                Ord.stringOrd
            )
            .values();
    }

    /**
     * Lookup and call the resolver applicable for the given batch.
     * @param <V> value class
     * @param <R> resolvable class
     * @param cls resolvable class
     * @param resolvables the batch of resolvables
     * @return a future representing the resolution
     */
    @SuppressWarnings("unchecked")
    private <V, R extends Resolvable<V>> List<Operation<R, V>> createOperations(
        List<R> batch
    ) {
        final Class<R> cls = (Class<R>) batch.index(0).getClass();
        final EngineResolver<E, V, R> resolver = registry.getOrThrow(cls);
        return resolver.createOperations(environment, batch);
    }

    /**
     * Inject every result of the given batch into the given node.
     * @param <V> value class
     * @param <R> resolvable class
     * @param <T> target class
     * @param node the node to inject into
     * @param batch the batch future
     * @return a node that every element was injected into.
     */
    private static <V, R extends Resolvable<V>, T> Node<T> injectOperationResult(
        Node<T> node,
        Result<R, V> result
    ) {
        return node.injectValue(result.getResolvable(), result.getValue());
    }
}
