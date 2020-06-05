package haxlike.impl;

import fj.P2;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;
import haxlike.Resolvable;
import haxlike.Results;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ResultsImpl implements Results {
    private final HashMap<? extends Resolvable<?>, ?> values;

    public <V, R extends Resolvable<V>>ResultsImpl(List<P2<R, V>> values) {
        this(HashMap.<R, V>iterableHashMap(values));
    }

    public ResultsImpl() {
        this(HashMap.hashMap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, R extends Resolvable<V>> Option<V> get(R resolvable) {
        return ((HashMap<R, V>) values).get(resolvable);
    }

    @SuppressWarnings("unchecked")
    private <V, R extends Resolvable<V>> void forEach(BiConsumer<R, V> f) {
        final HashMap<R, V> view = (HashMap<R, V>) values;
        for (R r : view.keys()) {
            f.accept(r, view.get(r).some());
        }
    }

    @Override
    @SuppressWarnings("squid:S1319")
    public <V, R extends Resolvable<V>> void into(HashMap<R, V> target) {
        forEach(target::set);
    }

    static <R extends Resolvable<V>, V> ResultsImpl from(
        List<Results> results
    ) {
        final HashMap<R, V> values = HashMap.hashMap();
        results.forEach(r -> r.into(values));
        return new ResultsImpl(values);
    }
}
