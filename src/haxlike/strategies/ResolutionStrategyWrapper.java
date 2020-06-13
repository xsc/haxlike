package haxlike.strategies;

import fj.control.parallel.Strategy;
import fj.data.List;
import haxlike.ResolutionStrategy;
import haxlike.resolvers.Operation;
import haxlike.resolvers.Results;
import lombok.Value;

@Value
public class ResolutionStrategyWrapper implements ResolutionStrategy {
    Strategy<?> strategy;

    @Override
    @SuppressWarnings("unchecked")
    public <R, V> List<Results<R, V>> run(List<Operation<R, V>> operations) {
        return ((Strategy<Results<R, V>>) strategy).parMap1(
                Operation::runOperation,
                operations
            );
    }
}
