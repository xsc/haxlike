package haxlike.resolvers.impl;

import haxlike.Resolvable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ResolvableValue<P, T> implements Resolvable<T> {
    @NonNull
    private final String resolvableKey;

    @NonNull
    private final P ref;

    @Override
    public String toString() {
        return resolvableKey + "(" + ref + ")";
    }
}
