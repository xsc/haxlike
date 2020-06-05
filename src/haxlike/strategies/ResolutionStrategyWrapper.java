package haxlike.strategies;

import fj.control.parallel.Strategy;
import fj.data.List;
import haxlike.Operation;
import haxlike.ResolutionStrategy;
import haxlike.Resolvable;
import haxlike.Results;
import lombok.Value;

@Value
public class ResolutionStrategyWrapper implements ResolutionStrategy {
    Strategy<Results> strategy;

    @Override
    public <R extends Resolvable<V>, V> List<Results> run(
        List<Operation<R, V>> operations
    ) {
        return strategy.parMap1(Operation::runOperation, operations);
    }
}
