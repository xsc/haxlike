package haxlike.impl;

import fj.Ord;
import fj.control.parallel.Strategy;
import fj.data.HashSet;
import fj.data.List;
import haxlike.Engine;
import haxlike.EngineCache;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Results;
import haxlike.impl.EngineResolver.Operation;
import java.util.Optional;
import lombok.Value;

@Value
class EngineImpl<E> implements Engine {
    EngineRegistry<E> registry;
    E environment;
    Strategy<?> strategy;

    @Override
    public <T> T resolve(Node<T> node, EngineCache cache) {
        Node<T> n = node;
        while (!n.isResolved()) {
            n = resolveNext(n, cache);
        }
        return n.getValue();
    }

    /**
     * Resolve the next available batches of resolvables
     * @param <T> target class
     * @param node node to resolve
     * @return a node with elements resolved
     */
    private <T> Node<T> resolveNext(Node<T> node, EngineCache cache) {
        return Optional
            .of(node)
            .map(this::uniqueResolvables)
            .map(cache::removeCached)
            .map(this::selectNextBatches)
            .map(this::createAllOperations)
            .map(this::runOperations)
            .map(cache::updateAndGet)
            .map(node::injectValues)
            .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private <T, V, R extends Resolvable<V>> List<R> uniqueResolvables(
        Node<T> node
    ) {
        final HashSet<R> s = HashSet.empty();
        node.getResolvables().forEach(r -> s.set((R) r));
        return s.toList();
    }

    @SuppressWarnings("unchecked")
    private <V, R extends Resolvable<V>> Results<R, V> runOperations(
        List<Operation<R, V>> operations
    ) {
        final List<Results<R, V>> results =
            ((Strategy<Results<R, V>>) strategy).parMap1(
                    Operation::runOperation,
                    operations
                );
        return Results.merge(results);
    }

    private <V, R extends Resolvable<V>> List<List<R>> selectNextBatches(
        List<R> resolvables
    ) {
        return resolvables
            .groupBy(r -> r.getClass().getName(), Ord.stringOrd)
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
}
