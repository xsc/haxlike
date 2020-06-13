package haxlike.relations.impl;

import fj.F2;
import haxlike.relations.Parameter;
import lombok.Value;

@Value
public class ParameterImpl<P, V> implements Parameter<P, V> {
    F2<P, V, P> attachFunction;

    @Override
    public P attach(P parameters, V value) {
        return attachFunction.f(parameters, value);
    }
}
