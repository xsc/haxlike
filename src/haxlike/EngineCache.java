package haxlike;

import fj.data.List;

@SuppressWarnings("squid:S1452")
public interface EngineCache {
    /**
     * Remove cached resolvables from the list of resolvables.
     */
    <R extends Resolvable<?>> List<R> removeCached(List<R> resolvables);

    /**
     * Use the given map of results to update the internal cache, then return
     * the full cache.
     * @param <V> generic resolvable value class
     * @param <R> generic resolvable class
     * @param results newly calculated resolution results
     * @return the full internal cache.
     */
    Results updateAndGet(Results results);
}
