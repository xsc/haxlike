package haxlike.impl;

import fj.data.HashMap;
import fj.data.List;
import haxlike.EngineCache;
import haxlike.Resolvable;
import haxlike.resolvers.Results;

public class EngineCacheImpl implements EngineCache {
    private final HashMap<Resolvable<?>, ?> cache = HashMap.hashMap();

    @Override
    public <R extends Resolvable<?>> List<R> removeCached(List<R> resolvables) {
        return resolvables.removeAll(cache::contains);
    }

    @Override
    public <R extends Resolvable<V>, V> Results<R, V> updateAndGet(
        Results<R, V> results
    ) {
        return mergeResultsIntoCache(results);
    }

    @SuppressWarnings("unchecked")
    private <R extends Resolvable<V>, V> Results<R, V> mergeResultsIntoCache(
        Results<R, V> results
    ) {
        final HashMap<R, V> cacheRef = (HashMap<R, V>) cache;
        results.into(cacheRef);
        return new ResultsImpl<>(cacheRef);
    }
}
