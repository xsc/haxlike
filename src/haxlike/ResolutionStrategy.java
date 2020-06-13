package haxlike;

import fj.data.List;

/**
 * A resolution strategy can run a series operations and return a list
 * of their results. It can thus be used to introduce parallelism to
 * resolution.
 */
public interface ResolutionStrategy {
    List<Results> run(List<Operation> operations);
}
