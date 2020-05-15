package haxlike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface Resolver<E, V, R extends Resolvable<V>> {
    CompletableFuture<List<V>> resolveAll(E environment, List<R> resolvables);
}
