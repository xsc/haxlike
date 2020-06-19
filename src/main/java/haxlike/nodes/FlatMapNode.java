package haxlike.nodes;

import fj.F;
import fj.data.List;
import haxlike.Node;
import haxlike.Resolvable;
import haxlike.resolvers.Results;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Value;

@Value
public class FlatMapNode<T, R> implements Node<R> {
    Node<T> inner;
    F<T, ? extends Node<R>> f;

    @Override
    public List<Resolvable<?>> getResolvables() {
        return inner.getResolvables();
    }

    @Override
    public Node<R> injectValues(Results<Resolvable<?>, ?> results) {
        final Node<T> result = inner.injectValues(results);
        return result.isResolved()
            ? f.f(result.getValue())
            : new FlatMapNode<>(result, f);
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public R getValue() {
        throw new UnsupportedOperationException(
            "Cannot call 'getValue' on flatMap node."
        );
    }

    @Value
    public static class Resolved<T, R> implements Node<R> {
        Node<T> inner;
        F<T, ? extends Node<R>> f;

        // --- Cache Value
        private final AtomicReference<Node<R>> resolvedNode = new AtomicReference<>();

        private Node<R> resolved() {
            return resolvedNode.updateAndGet(
                value -> value == null ? f.f(inner.getValue()) : value
            );
        }

        @Override
        public List<Resolvable<?>> getResolvables() {
            return resolved().getResolvables();
        }

        @Override
        public Node<R> injectValues(Results<Resolvable<?>, ?> results) {
            return resolved().injectValues(results);
        }

        @Override
        public boolean isResolved() {
            return false;
        }

        @Override
        public R getValue() {
            throw new UnsupportedOperationException(
                "Cannot call 'getValue' on flatMap node."
            );
        }
    }
}
