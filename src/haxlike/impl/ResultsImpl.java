package haxlike.impl;

import fj.F;
import fj.P2;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;
import haxlike.resolvers.Results;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ResultsImpl<R, V> implements Results<R, V> {
    private final HashMap<R, V> values;

    public ResultsImpl(List<P2<R, V>> values) {
        this(HashMap.iterableHashMap(values));
    }

    public ResultsImpl() {
        this(HashMap.hashMap());
    }

    @Override
    public Option<V> get(R resolvable) {
        return values.get(resolvable);
    }

    private void forEach(BiConsumer<R, V> f) {
        for (R r : values.keys()) {
            f.accept(r, values.get(r).some());
        }
    }

    @Override
    public <T> Results<T, V> mapKeys(F<R, T> f) {
        final HashMap<T, V> target = HashMap.hashMap();
        forEach((k, v) -> target.set(f.f(k), v));
        return new ResultsImpl<>(target);
    }

    @Override
    public void into(HashMap<R, V> target) {
        forEach(target::set);
    }

    static <R, V> Results<R, V> from(List<Results<R, V>> results) {
        final HashMap<R, V> values = HashMap.hashMap();
        results.forEach(r -> r.into(values));
        return new ResultsImpl<>(values);
    }
}
