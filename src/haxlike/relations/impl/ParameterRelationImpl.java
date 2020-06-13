package haxlike.relations.impl;

import fj.F;
import fj.F2;
import haxlike.Node;
import haxlike.relations.Parameter;
import haxlike.relations.ParameterRelation;
import lombok.Value;

@Value
public class ParameterRelationImpl<T, P, V>
    implements ParameterRelation<T, P, V> {
    F2<T, V, T> attachFunction;
    F2<T, P, Node<V>> parameterizedNodeFunction;
    P parameters;

    @Override
    public F<T, Node<V>> getNodeFunction() {
        return value -> parameterizedNodeFunction.f(value, parameters);
    }

    @Override
    public <N> ParameterRelation<T, P, V> with(
        Parameter<P, N> parameter,
        N value
    ) {
        return new ParameterRelationImpl<>(
            attachFunction,
            parameterizedNodeFunction,
            parameter.attach(parameters, value)
        );
    }
}
