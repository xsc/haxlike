package haxlike;

import fj.data.List;

/**
 * A selection strategy defines which batches are resolved in any given iteration.
 *
 * One iteration consists of:
 * <ol>
 *   <li>Collect all resolvables</li>
 *   <li>Group them into batches (by class)</li>
 *   <li>Select batches to resolve</li>
 *   <li>Create batch results</li>
 *   <li>Inject results back into the node.</li>
 * </ol>
 *
 * Selection strategies can thus be used to defer resolution of resolvables that
 * could be more efficiently resolved at a later point in time.
 * {@link SelectionStrategies#defaultStrategy()} and {@link SelectionStrategies#maxStrategy(int)}
 * will be sufficient for most use cases, rather than fine-tuning resolution order.
 */
public interface SelectionStrategy {
    <V, R extends Resolvable<V>> List<List<R>> select(List<List<R>> batches);
}
