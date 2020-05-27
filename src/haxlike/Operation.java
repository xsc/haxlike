package haxlike;

import fj.F0;
import fj.data.List;
import lombok.Value;

@Value
public class Operation<R extends Resolvable<V>, V> {
    List<R> batch;
    F0<Results<R, V>> op;

    public final Results<R, V> runOperation() {
        return op.f();
    }

    @SuppressWarnings("unchecked")
    public Class<R> getResolvableClass() {
        return (Class<R>) batch.head().getClass();
    }
}
