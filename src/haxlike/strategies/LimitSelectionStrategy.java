package haxlike.strategies;

import fj.data.List;
import haxlike.Resolvable;
import haxlike.SelectionStrategy;
import lombok.Value;

@Value
public class LimitSelectionStrategy implements SelectionStrategy {
    int numberOfBatches;

    @Override
    public <V, R extends Resolvable<V>> List<List<R>> select(
        List<List<R>> batches
    ) {
        return batches.take(numberOfBatches);
    }
}
