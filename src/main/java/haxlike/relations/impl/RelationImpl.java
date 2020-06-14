package haxlike.relations.impl;

import fj.F;
import fj.F2;
import haxlike.Node;
import haxlike.relations.Relation;
import lombok.Value;

@Value
public class RelationImpl<T, V> implements Relation<T, V> {
    F2<T, V, T> attachFunction;
    F<T, Node<V>> nodeFunction;
}
