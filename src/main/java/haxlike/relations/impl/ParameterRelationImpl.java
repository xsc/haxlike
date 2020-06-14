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
    F<P, Node<V>> nodeFunction;
    F<T, P> parameterFunction;

    @Override
    public F<T, Node<V>> getNodeFunction() {
        return value -> nodeFunction.f(parameterFunction.f(value));
    }

    @Override
    public <N> ParameterRelation<T, P, V> with(
        Parameter<P, N> parameter,
        N parameterValue
    ) {
        return new ParameterRelationImpl<>(
            attachFunction,
            nodeFunction,
            value -> {
                final P parameters = parameterFunction.f(value);
                return parameter.attach(parameters, parameterValue);
            }
        );
    }
}
