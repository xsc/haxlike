package haxlike.resolvers;

import static fj.P.p;

import fj.F;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;
import haxlike.Resolvable;
import haxlike.impl.ResultsImpl;

public interface Results<R, V> {
    Option<V> get(R value);

    void into(HashMap<R, V> target);

    <T> Results<T, V> mapKeys(F<R, T> f);

    default V getSome(R value) {
        return this.get(value).some();
    }

    // --- Factories
    public static <R, V> Results<R, V> empty() {
        return new ResultsImpl<>();
    }

    public static <R, V> Results<R, V> single(R r, V v) {
        return new ResultsImpl<>(List.single(p(r, v)));
    }

    public static <R, V> Results<R, V> map(List<R> resolvables, F<R, V> f) {
        return Results.zip(resolvables, resolvables.map(f));
    }

    public static <R, V> Results<R, V> zip(
        List<R> resolvables,
        List<V> results
    ) {
        return new ResultsImpl<>(resolvables.zip(results));
    }

    public static <R, V, I> Results<R, V> match(
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

    public static <R extends Resolvable<V>, V, I> Results<R, V> match(
        List<R> resolvables,
        F<R, I> fr,
        List<V> results,
        F<V, I> fv
    ) {
        return Results.match(resolvables, fr, results, fv, null);
    }
}
