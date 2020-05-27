package haxlike;

import haxlike.strategies.DefaultSelectionStrategy;
import haxlike.strategies.LimitSelectionStrategy;
import haxlike.strategies.PrioritySelectionStrategy;

/**
 * Predefined {@link SelectionStrategy} implementations.
 */
public class SelectionStrategies {

    /**
     * Return the default selection strategy, always resolving all available batches.
     * @return a SelectionStrategy that selects all batches it is passed.
     */
    public static SelectionStrategy defaultStrategy() {
        return DefaultSelectionStrategy.INSTANCE;
    }

    /**
     * Create a selection strategy that will resolve up to the given number of batches
     * per iteration.
     * @param numberOfBatches the maximum number of batches to resolve
     * @return a SelectionStrategy selecting the given number of batches
     */
    public static SelectionStrategy maxStrategy(int numberOfBatches) {
        return new LimitSelectionStrategy(numberOfBatches);
    }

    /**
     * Create a selection strategy that will use the given map of priorities to decide on
     * what to resolve next:
     * - Default priority (for things not in the map) is 0.
     * - Higher priorities will be resolved first.
     * - Classes with the same priority will be resolved in the same iteration.
     * Call {@link PriorityStrategy#withPriority(Class, Integer)} to add new entries.
     * @return a SelectionStrategy taking priorities into account.
     */
    public static PrioritySelectionStrategy priorityStrategy() {
        return new PrioritySelectionStrategy();
    }

    // ---
    private SelectionStrategies() {}
}
