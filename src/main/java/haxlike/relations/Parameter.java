package haxlike.relations;

import fj.F2;
import haxlike.relations.impl.ParameterImpl;

public interface Parameter<P, V> {
    P attach(P parameters, V value);

    static <P, V> Parameter<P, V> declare(F2<P, V, P> attachFunction) {
        return new ParameterImpl<>(attachFunction);
    }
}
