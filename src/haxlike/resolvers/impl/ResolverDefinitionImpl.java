package haxlike.resolvers.impl;

import fj.data.List;
import haxlike.Resolvable;
import haxlike.resolvers.Operation;
import haxlike.resolvers.ResolverDefinition;
import haxlike.resolvers.ResolverFunction;
import lombok.Value;

@Value
public class ResolverDefinitionImpl<E, R extends Resolvable<V>, V>
    implements ResolverDefinition<E, R, V> {
    Class<R> cls;
    ResolverFunction.OperationResolver<E, R, V> f;

    public ResolverDefinitionImpl(Class<R> cls, ResolverFunction<E, R, V> f) {
        this.cls = cls;
        this.f = f.toOperationResolver();
    }

    @Override
    public String getResolvableKey() {
        return cls.getName();
    }

    @Override
    public List<Operation<R, V>> createOperations(E env, List<R> batch) {
        return f.createOperations(env, batch);
    }
}
