package haxlike.impl;

import fj.Ord;
import fj.data.HashSet;
import fj.data.List;
import haxlike.Engine;
import haxlike.EngineCache;
import haxlike.Node;
import haxlike.Operation;
import haxlike.ResolutionStrategy;
import haxlike.Resolvable;
import haxlike.Results;
import haxlike.SelectionStrategy;
import java.util.Optional;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Builder
@Slf4j
class EngineImpl<E> implements Engine {
    EngineRegistry<E> registry;
    E environment;
    ResolutionStrategy resolutionStrategy;
    SelectionStrategy selectionStrategy;
    int maxIterationCount;

    @Override
    public <T> T resolve(Node<T> node, EngineCache cache) {
        Node<T> n = node;
        int iterationCount = 1;
        while (!n.isResolved()) {
            verifyMaxDepth(iterationCount);
            logIteration(iterationCount++);
            n = resolveNext(n, cache);
        }
        return n.getValue();
    }

    private void verifyMaxDepth(int iterationCount) {
        if (iterationCount > maxIterationCount) {
            throw new IllegalStateException(
                String.format(
                    "Maximum iteration count of %d has been exceeded.",
                    maxIterationCount
                )
            );
        }
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
            .map(this::logResolvables)
            .map(cache::removeCached)
            .map(this::selectNextBatches)
            .map(this::logBatches)
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

    private <V, R extends Resolvable<V>> Results<R, V> runOperations(
        List<Operation<R, V>> operations
    ) {
        return Results.merge(resolutionStrategy.run(operations));
    }

    private <V, R extends Resolvable<V>> List<List<R>> selectNextBatches(
        List<R> resolvables
    ) {
        final List<List<R>> allBatches = resolvables
            .groupBy(r -> r.getClass().getName(), Ord.stringOrd)
            .values();
        return selectionStrategy.select(allBatches);
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

    // --- Logging
    private void logIteration(int iterationCount) {
        log.trace("--- Iteration #{}", iterationCount);
    }

    private <V, R extends Resolvable<V>> List<R> logResolvables(
        List<R> resolvables
    ) {
        log.trace("Resolvables: {}", resolvables);
        return resolvables;
    }

    private <V, R extends Resolvable<V>> List<List<R>> logBatches(
        List<List<R>> batches
    ) {
        if (batches.isEmpty()) {
            log.trace("=> No batches to resolve (might be cached).");
        } else {
            batches
                .zipIndex()
                .forEach(p -> log.trace("=> Batch[{}]: {}", p._2(), p._1()));
        }
        return batches;
    }
}
