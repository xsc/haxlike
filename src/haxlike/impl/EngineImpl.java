package haxlike.impl;

import fj.Ord;
import fj.control.parallel.Strategy;
import fj.data.HashMap;
import fj.data.List;
import haxlike.Engine;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.impl.EngineResolver.Operation;
import haxlike.impl.EngineResolver.Result;
import java.util.Optional;
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
    private <T> Node<T> resolveNext(Node<T> node) {
        return Optional
            .of(node)
            .map(this::selectNextBatches)
            .map(this::createAllOperations)
            .map(this::runOperations)
            .map(this::zipResults)
            .map(node::injectValues)
            .orElseThrow();
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

    private <V, R extends Resolvable<V>> List<Operation<R, V>> createAllOperations(
        List<List<R>> batches
    ) {
        return batches.bind(this::createOperations);
    }

    @SuppressWarnings("unchecked")
    private <V, R extends Resolvable<V>> List<Operation<R, V>> createOperations(
        List<R> batch
    ) {
        final Class<R> cls = (Class<R>) batch.index(0).getClass();
        final EngineResolver<E, V, R> resolver = registry.getOrThrow(cls);
        return resolver.createOperations(environment, batch);
    }

    private <V, R extends Resolvable<V>> HashMap<Resolvable<V>, V> zipResults(
        List<Result<R, V>> results
    ) {
        final HashMap<Resolvable<V>, V> m = HashMap.hashMap();
        results.forEach(r -> m.set(r.getResolvable(), r.getValue()));
        return m;
    }
}
