package haxlike;

public interface SingleResolver<E, V, R extends Resolvable<V>> {
    V resolve(E env, R resolvable);
}
