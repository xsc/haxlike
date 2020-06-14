package haxlike;

import fj.data.List;
import haxlike.resolvers.Operation;
import haxlike.resolvers.Results;

/**
 * A resolution strategy can run a series operations and return a list
 * of their results. It can thus be used to introduce parallelism to
 * resolution.
 */
public interface ResolutionStrategy {
    <R, V> List<Results<R, V>> run(List<Operation<R, V>> operations);
}
