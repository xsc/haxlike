package haxlike;

import fj.data.List;
import haxlike.impl.EngineCacheImpl;
import haxlike.resolvers.Results;

public final class EngineCaches {

    public static EngineCache defaultCache() {
        return new EngineCacheImpl();
    }

    public static EngineCache noCache() {
        return new NoCache();
    }

    private static class NoCache implements EngineCache {

        @Override
        public <R extends Resolvable<V>, V> Results<R, V> updateAndGet(
            Results<R, V> results
        ) {
            return results;
        }

        @Override
        public <R extends Resolvable<?>> List<R> removeCached(
            List<R> resolvables
        ) {
            return resolvables;
        }
    }

    private EngineCaches() {}
}
