package haxlike;

import java.util.List;
import java.util.stream.Collectors;

public class Engine {

    public static <T> T resolve(Node<T> node) {
        Node<T> n = node;
        while (!n.hasValue()) {
            n = resolveNext(n);
        }
        return n.getValue();
    }

    private static <T, V, R extends Resolvable<V, R>> Node<T> resolveNext(
        Node<T> node
    ) {
        final List<R> batch = selectBatch(node);
        final List<V> results = batch.get(0).resolveAll(batch);

        Node<T> n = node;
        for (int i = 0; i < results.size(); i++) {
            n = n.injectValue(batch.get(i), results.get(i));
        }
        return n;
    }

    @SuppressWarnings("unchecked")
    private static <T, V, R extends Resolvable<V, R>> List<R> selectBatch(
        Node<T> n
    ) {
        final List<Resolvable<?, ?>> resolvables = n.allResolvables();
        final Class<R> cls = (Class<R>) resolvables.get(0).getClass();
        return resolvables
            .stream()
            .filter(r -> r.getClass().equals(cls))
            .map(cls::cast)
            .distinct()
            .collect(Collectors.toList());
    }

    private Engine() {}
}
