package haxlike.impl;

import haxlike.Engine;
import haxlike.Node;
import haxlike.Resolvable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;

@Value
class EngineInstance<E> implements Engine.Instance {
    EngineRegistry<E> registry;
    E environment;

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
        return selectNextClasses(node)
            .map(cls -> this.resolveBatchByClass(node, cls))
            .collect(Collectors.toList())
            .stream()
            .reduce(node, EngineInstance::injectBatch, (a, b) -> b);
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
    private static <T, V, R extends Resolvable<V>> Stream<Class<R>> selectNextClasses(
        Node<T> node
    ) {
        final List<Resolvable<?>> resolvables = node.allResolvables();
        return resolvables
            .stream()
            .map(r -> (Class<R>) r.getClass())
            .distinct()
            .limit(2);
    }

    /**
     * Resolve a single class from the resolvables within the given node.
     * @param <V> value class
     * @param <R> resolvable class
     * @param <T> target class
     * @param node the node to resolve
     * @param cls the resolvable class to resolve
     * @return a batch future representing the resolution
     */
    private <V, R extends Resolvable<V>, T> BatchFuture<V, R> resolveBatchByClass(
        Node<T> node,
        Class<R> cls
    ) {
        final List<R> resolvables = node
            .allResolvables()
            .stream()
            .filter(r -> cls.equals(r.getClass()))
            .map(cls::cast)
            .collect(Collectors.toList());

        return this.resolveBatch(resolvables, cls);
    }

    /**
     * Lookup and call the resolver applicable for the given batch.
     * @param <V> value class
     * @param <R> resolvable class
     * @param cls resolvable class
     * @param resolvables the batch of resolvables
     * @return a future representing the resolution
     */
    private <V, R extends Resolvable<V>> BatchFuture<V, R> resolveBatch(
        List<R> resolvables,
        Class<R> cls
    ) {
        final CompletableFuture<List<V>> future = registry
            .get(cls)
            .orElseThrow()
            .resolveAll(environment, resolvables);
        return new BatchFuture<>(resolvables, future);
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
    private static <V, R extends Resolvable<V>, T> Node<T> injectBatch(
        Node<T> node,
        BatchFuture<V, R> batch
    ) {
        final List<R> resolvables = batch.getResolvables();
        final List<V> values = batch.getFuture().join();

        Node<T> n = node;
        for (int i = 0; i < values.size(); i++) {
            n = n.injectValue(resolvables.get(i), values.get(i));
        }
        return n;
    }
}
