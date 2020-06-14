package haxlike.relations;

/**
 * A parameterised relation.
 *
 * @param <T> container class
 * @param <P> parameter class
 * @param <V> relation value class
 */
public interface ParameterRelation<T, P, V> extends Relation<T, V> {
    <N> ParameterRelation<T, P, V> with(Parameter<P, N> parameter, N value);
}
