package haxlike;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Engine {
    private static final int PARALLELISM = 2;

    /**
     * Resolve the given node
     * @param <T> target value class
     * @param node node to resolve
     * @return the resolved value
     */
    public static <T> T resolve(Node<T> node) {
        Node<T> n = node;
        while (!n.hasValue()) {
            n = resolveNext(n);
        }
        return n.getValue();
    }

    /**
     * Resolve the next available batches of resolvables
     */
    private static <T, V, R extends Resolvable<V, R>> Node<T> resolveNext(
        Node<T> node
    ) {
        return selectBatches(node)
            .map(Engine::resolveBatch)
            .collect(Collectors.toList())
            .stream()
            .reduce(node, (n, p) -> p.join(n), (a, b) -> b);
    }

    /**
     * Asynchronously resolve the given batch
     * @param <V> resolvable value class
     * @param <R> resolvable class
     * @param resolvables the batch to resolve
     * @return a batch in progress
     */
    private static <V, R extends Resolvable<V, R>> BatchInProgress<V, R> resolveBatch(
        List<R> resolvables
    ) {
        return new BatchInProgress<>(
            resolvables,
            resolvables.get(0).resolveAll(resolvables)
        );
    }

    /**
     * Create a stream of batches to resolve, based on the desired parallelism.
     *
     * @param <T>  target class
     * @param <V>  resolvable value class
     * @param <R>  resolvable class
     * @param node node to select batches for
     * @return stream of batches
     */
    @SuppressWarnings("unchecked")
    private static <T, V, R extends Resolvable<V, R>> Stream<List<R>> selectBatches(
        Node<T> node
    ) {
        final List<Resolvable<?, ?>> resolvables = node.allResolvables();
        return resolvables
            .stream()
            .map(Object::getClass)
            .distinct()
            .limit(PARALLELISM)
            .map(cls -> filterBatch((Class<R>) cls, resolvables));
    }

    /**
     * Filter the given resolvables by class.
     */
    private static <V, R extends Resolvable<V, R>> List<R> filterBatch(
        Class<R> cls,
        List<Resolvable<?, ?>> resolvables
    ) {
        return resolvables
            .stream()
            .filter(r -> r.getClass().equals(cls))
            .map(cls::cast)
            .distinct()
            .collect(Collectors.toList());
    }

    // --- Batch
    private static class BatchInProgress<V, R extends Resolvable<V, R>> {
        private final List<R> resolvables;
        private final CompletableFuture<List<V>> future;

        BatchInProgress(
            List<R> resolvables,
            CompletableFuture<List<V>> future
        ) {
            this.resolvables = resolvables;
            this.future = future;
        }

        <N> Node<N> join(Node<N> node) {
            Node<N> n = node;
            List<V> values = future.join();
            for (int i = 0; i < values.size(); i++) {
                n = n.injectValue(resolvables.get(i), values.get(i));
            }
            return n;
        }
    }

    // --- Hidden
    private Engine() {}
}
