package haxlike.impl;

import fj.F;
import fj.F2;
import haxlike.Relation;
import haxlike.Resolvable;
import lombok.Value;

@Value
public class RelationImpl<T, V, R extends Resolvable<V>>
    implements Relation<T, V, R> {
    F2<T, V, T> attachFunction;
    F<T, R> nodeFunction;
}
