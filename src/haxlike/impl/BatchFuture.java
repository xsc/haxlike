package haxlike.impl;

import haxlike.Resolvable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Value;

/**
 * Representation of a resolution in-flight.
 *
 * @param <V>
 * @param <R>
 */
@Value
class BatchFuture<V, R extends Resolvable<V>> {
    List<R> resolvables;
    CompletableFuture<List<V>> future;
}
