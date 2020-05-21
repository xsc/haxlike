package haxlike;

import fj.data.HashMap;
import fj.data.List;

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
    <V, R extends Resolvable<V>> HashMap<R, V> updateAndGet(
        HashMap<R, V> results
    );
}
