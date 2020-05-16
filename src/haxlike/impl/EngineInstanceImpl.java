package haxlike.impl;

import static fj.P.p;

import fj.Ord;
import fj.P2;
import fj.control.parallel.Strategy;
import fj.data.List;
import haxlike.Engine;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.Resolver;
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
    @SuppressWarnings("unchecked")
    private <T, V, R extends Resolvable<V>> Node<T> resolveNext(Node<T> node) {
        final List<List<R>> batches = selectNextBatches(node);
        return ((Strategy<P2<List<R>, List<V>>>) strategy).parMap1(
                this::resolveBatch,
                batches
            )
            .foldLeft(EngineImpl::injectBatch, node);
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
    private <V, R extends Resolvable<V>> P2<List<R>, List<V>> resolveBatch(
        List<R> resolvables
    ) {
        final Class<R> cls = (Class<R>) resolvables.index(0).getClass();
        final Resolver<E, V, R> resolver = registry.getOrThrow(cls);
        return p(resolvables, resolver.resolveAll(environment, resolvables));
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
        P2<List<R>, List<V>> batch
    ) {
        final List<R> resolvables = batch._1();
        final List<V> values = batch._2();

        return resolvables
            .zip(values)
            .foldLeft((n, p) -> n.injectValue(p._1(), p._2()), node);
    }
}
