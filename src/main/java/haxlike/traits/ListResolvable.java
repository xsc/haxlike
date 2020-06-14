package haxlike.traits;

import fj.data.List;
import haxlike.Resolvable;

/**
 * Convenience trait, enriching a {@link Resolvable} returning a list with
 * {@link ListNode} functionality, including {@link ListNode#mapEach},
 * {@link ListNode#flatMapEach} and others.
 *
 * @param <T> list element class
 */
public interface ListResolvable<T> extends Resolvable<List<T>>, ListNode<T> {}
