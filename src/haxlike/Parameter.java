package haxlike;

import fj.F2;

/**
 * A parameter is a transformation on a resolvable that occurs before resolution.
 * @param <N>
 * @param <V>
 */
@FunctionalInterface
public interface Parameter<R extends Resolvable<?>, P> {
    <T extends R> T attach(T node, P value);

    @SuppressWarnings("unchecked")
    static <R extends Resolvable<?>, P> Parameter<R, P> declare(
        F2<R, P, R> attachFunction
    ) {
        return new Parameter<>() {

            public <T extends R> T attach(T node, P value) {
                return (T) attachFunction.f((R) node, value);
            }
        };
    }
}
