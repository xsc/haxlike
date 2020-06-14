package haxlike;

import fj.control.parallel.Strategy;
import haxlike.strategies.ResolutionStrategyWrapper;
import java.util.concurrent.ExecutorService;

public class ResolutionStrategies {
    private static final ResolutionStrategy DEFAULT = new ResolutionStrategyWrapper(
        Strategy.seqStrategy()
    );

    /**
     * Default resolution strategy, performing sequential resolution.
     * @return a resolution strategy
     */
    public static ResolutionStrategy defaultStrategy() {
        return DEFAULT;
    }

    /**
     * Resolution strategy using an {@link ExecutorService} to resolve the single
     * elements.
     * @param e executor to use
     * @return a resolution strategy
     */
    public static ResolutionStrategy executorServiceStrategy(
        ExecutorService e
    ) {
        return new ResolutionStrategyWrapper(Strategy.executorStrategy(e));
    }

    private ResolutionStrategies() {}
}
