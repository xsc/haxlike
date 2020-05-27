package haxlike.strategies;

import fj.Ord;
import fj.data.List;
import fj.data.TreeMap;
import haxlike.Resolvable;
import haxlike.SelectionStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.With;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@With(AccessLevel.PRIVATE)
public class PrioritySelectionStrategy implements SelectionStrategy {
    private static final Ord<Class<?>> CLASS_ORD = Ord.ord(
        (a, b) -> Ord.stringOrd.compare(a.getName(), b.getName())
    );

    //  --- Constructor
    private final TreeMap<Class<?>, Integer> priorities;

    public PrioritySelectionStrategy() {
        this(TreeMap.empty(CLASS_ORD));
    }

    // --- Adding priorities
    public PrioritySelectionStrategy withPriority(
        Class<?> cls,
        Integer priority
    ) {
        return this.withPriorities(priorities.set(cls, priority));
    }

    public PrioritySelectionStrategy withPriorityHigherThan(
        Class<?> cls,
        Class<?> higherThan
    ) {
        return this.withPriority(cls, toPriority(higherThan) + 1);
    }

    public PrioritySelectionStrategy withPriorityLowerThan(
        Class<?> cls,
        Class<?> lowerThan
    ) {
        return this.withPriority(cls, toPriority(lowerThan) - 1);
    }

    // --- Selection
    @Override
    public <V, R extends Resolvable<V>> List<List<R>> select(
        List<List<R>> batches
    ) {
        return batches.groupBy(this::toPriority, Ord.intOrd).max().some()._2();
    }

    private <R> Integer toPriority(List<R> batch) {
        final Class<?> cls = batch.head().getClass();
        return toPriority(cls);
    }

    private Integer toPriority(Class<?> cls) {
        return priorities.get(cls).orSome(0);
    }
}
