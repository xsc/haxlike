package haxlike.impl;

import fj.Ord;
import fj.data.HashSet;
import fj.data.List;
import haxlike.Engine;
import haxlike.EngineCache;
import haxlike.Node;
import haxlike.ResolutionStrategy;
import haxlike.Resolvable;
import haxlike.SelectionStrategy;
import haxlike.resolvers.Operation;
import haxlike.resolvers.ResolverDefinition;
import haxlike.resolvers.Results;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
class EngineImpl<E> implements Engine {
    @NonNull
    EngineLogger logger;

    @NonNull
    EngineRegistry<E> registry;

    @NonNull
    ResolutionStrategy resolutionStrategy;

    @NonNull
    SelectionStrategy selectionStrategy;

    @NonNull
    Integer maxIterationCount;

    E environment;

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
     *
     * @param <T>  target class
     * @param node node to resolve
     * @return a node with elements resolved
     */
    private <T> Node<T> resolveNext(Node<T> node, EngineCache cache) {
        return Optional
            .of(node)
            .map(this::uniqueResolvables)
            .map(this::logResolvables)
            .map(cache::removeCached)
            .map(this::logUncachedResolvables)
            .map(this::selectNextBatches)
            .map(this::logBatches)
            .map(this::createAllOperations)
            .map(this::runOperations)
            .map(cache::updateAndGet)
            .map(results -> injectResults(node, results))
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

    private <R, V> Results<R, V> runOperations(
        List<Operation<R, V>> operations
    ) {
        return ResultsImpl.from(resolutionStrategy.run(operations));
    }

    private <V, R extends Resolvable<V>> List<List<R>> selectNextBatches(
        List<R> resolvables
    ) {
        final List<List<R>> allBatches = resolvables
            .groupBy(Resolvable::getResolvableKey, Ord.stringOrd)
            .values();
        return selectionStrategy.select(allBatches);
    }

    private <V, R extends Resolvable<V>> List<Operation<R, V>> createAllOperations(
        List<List<R>> batches
    ) {
        return batches.bind(this::createOperations);
    }

    private <V, R extends Resolvable<V>> List<Operation<R, V>> createOperations(
        List<R> batch
    ) {
        final ResolverDefinition<E, R, V> resolver = registry.getResolverOrThrow(
            batch.head()
        );
        return resolver.createOperations(environment, batch);
    }

    @SuppressWarnings("unchecked")
    private <T, R extends Resolvable<V>, V> Node<T> injectResults(
        Node<T> node,
        Results<R, V> results
    ) {
        return node.injectValues((Results<Resolvable<?>, ?>) results);
    }

    // --- Logging
    private void logIteration(int iterationCount) {
        log("--- Iteration #{}", iterationCount);
    }

    private <V, R extends Resolvable<V>> List<R> logResolvables(
        List<R> resolvables
    ) {
        log("Resolvables: {}", resolvables);
        return resolvables;
    }

    private <V, R extends Resolvable<V>> List<R> logUncachedResolvables(
        List<R> resolvables
    ) {
        if (resolvables.isEmpty()) {
            log("=> All results are already cached.");
        } else if (resolvables.length() == 1) {
            log("=> 1 value needs to be resolved.");
        } else {
            log("=> {} values need to be resolved.", resolvables.length());
        }
        return resolvables;
    }

    private <V, R extends Resolvable<V>> List<List<R>> logBatches(
        List<List<R>> batches
    ) {
        if (batches.isNotEmpty()) {
            batches
                .zipIndex()
                .forEach(p -> log("=> Batch[{}]: {}", p._2(), p._1()));
        }
        return batches;
    }

    private void log(String fmt, Object... args) {
        this.getLogger().log(fmt, args);
    }
}
