package haxlike;

import static fj.P.p;

import fj.F;
import fj.P2;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;
import java.util.function.BiConsumer;

public class Results<R, V> {
    private final HashMap<R, V> values;

    private Results(List<P2<R, V>> tuples) {
        this(HashMap.iterableHashMap(tuples));
    }

    private Results(HashMap<R, V> values) {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resolvable<?>> Option<V> get(T resolvable) {
        return values.get((R) resolvable);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resolvable<?>> V getSome(T resolvable) {
        return values.get((R) resolvable).some();
    }

    public void forEach(BiConsumer<R, V> f) {
        for (R r : values.keys()) {
            f.accept(r, values.get(r).some());
        }
    }

    @SuppressWarnings("squid:S1319")
    public void into(HashMap<R, V> target) {
        forEach(target::set);
    }

    // --- Constructors
    public static <R, V> Results<R, V> empty() {
        return new Results<>(List.nil());
    }

    public static <R, V> Results<R, V> single(R r, V v) {
        return new Results<>(List.single(p(r, v)));
    }

    public static <R, V> Results<R, V> map(List<R> resolvables, F<R, V> f) {
        return Results.zip(resolvables, resolvables.map(f));
    }

    public static <R, V> Results<R, V> zip(
        List<R> resolvables,
        List<V> results
    ) {
        return new Results<>(resolvables.zip(results));
    }

    // --- Wrapper around results
    @SuppressWarnings("squid:S1319")
    public static <R, V> Results<R, V> wrap(HashMap<R, V> values) {
        return new Results<>(values);
    }

    // --- Merge
    public static <R, V> Results<R, V> merge(Results<R, V> a, Results<R, V> b) {
        final HashMap<R, V> values = HashMap.hashMap();
        a.into(values);
        b.into(values);
        return new Results<>(values);
    }

    public static <R, V> Results<R, V> merge(List<Results<R, V>> results) {
        final HashMap<R, V> values = HashMap.hashMap();
        results.forEach(r -> r.into(values));
        return new Results<>(values);
    }
}
