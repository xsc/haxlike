package haxlike.impl;

import fj.F;
import fj.F2;
import haxlike.Node;
import haxlike.Relation;
import lombok.Value;

@Value
public class RelationImpl<T, R> implements Relation<T, R> {
    F2<T, R, T> attachFunction;
    F<T, Node<R>> nodeFunction;
}
