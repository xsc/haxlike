package haxlike.impl;

import fj.data.HashMap;
import fj.data.List;
import haxlike.EngineCache;
import haxlike.Resolvable;
import haxlike.Results;

public class EngineCacheImpl implements EngineCache {
    private final HashMap<Resolvable<?>, ?> cache = HashMap.hashMap();

    @Override
    public <R extends Resolvable<?>> List<R> removeCached(List<R> resolvables) {
        return resolvables.removeAll(cache::contains);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> Results<R, V> updateAndGet(
        Results<R, V> results
    ) {
        final HashMap<R, V> cacheRef = (HashMap<R, V>) cache;
        results.into(cacheRef);
        return Results.wrap(cacheRef);
    }
}
