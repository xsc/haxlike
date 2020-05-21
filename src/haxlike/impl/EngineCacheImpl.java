package haxlike.impl;

import fj.data.HashMap;
import fj.data.List;
import haxlike.EngineCache;
import haxlike.Resolvable;

public class EngineCacheImpl implements EngineCache {
    private final HashMap<Resolvable<?>, ?> cache = HashMap.hashMap();

    public <R extends Resolvable<?>> List<R> removeCached(List<R> resolvables) {
        return resolvables.removeAll(cache::contains);
    }

    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> HashMap<R, V> updateAndGet(
        HashMap<R, V> results
    ) {
        final HashMap<R, V> cacheRef = (HashMap<R, V>) cache;
        results.forEach(k -> cacheRef.set(k, results.get(k).some()));
        return cacheRef;
    }
}
