package haxlike;

import static fj.P.p;

import fj.F;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;
import haxlike.impl.ResultsImpl;

public interface Results {
    <V, T extends Resolvable<V>> Option<V> get(T resolvable);

    <V, R extends Resolvable<V>> void into(HashMap<R, V> target);

    default <V, T extends Resolvable<V>> V getSome(T resolvable) {
        return this.get(resolvable).some();
    }

    // --- Factories
    public static Results empty() {
        return new ResultsImpl();
    }

    public static <R extends Resolvable<V>, V> Results single(R r, V v) {
        return new ResultsImpl(List.single(p(r, v)));
    }

    public static <R extends Resolvable<V>, V> Results map(
        List<R> resolvables,
        F<R, V> f
    ) {
        return Results.zip(resolvables, resolvables.map(f));
    }

    public static <R extends Resolvable<V>, V> Results zip(
        List<R> resolvables,
        List<V> results
    ) {
        return new ResultsImpl(resolvables.zip(results));
    }

    public static <R extends Resolvable<V>, V, I> Results match(
        List<R> resolvables,
        F<R, I> fr,
        List<V> results,
        F<V, I> fv,
        V defaultValue
    ) {
        final HashMap<I, V> lookup = HashMap.iterableHashMap(
            results.map(fv).zip(results)
        );
        return Results.zip(
            resolvables,
            resolvables.map(fr).map(i -> lookup.get(i).orSome(defaultValue))
        );
    }

    public static <R extends Resolvable<V>, V, I> Results match(
        List<R> resolvables,
        F<R, I> fr,
        List<V> results,
        F<V, I> fv
    ) {
        return Results.match(resolvables, fr, results, fv, null);
    }
}
