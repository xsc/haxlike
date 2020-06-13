package haxlike.resolvers;

@FunctionalInterface
public interface Operation<R, V> {
    Results<R, V> runOperation();
}
