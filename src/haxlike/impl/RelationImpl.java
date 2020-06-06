package haxlike.impl;

import fj.F;
import fj.F2;
import haxlike.Node;
import haxlike.Relation;
import lombok.Value;

@Value
public class RelationImpl<T, R, N extends Node<R>>
    implements Relation<T, R, N> {
    F2<T, R, T> attachFunction;
    F<T, N> nodeFunction;
}
