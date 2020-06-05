package haxlike.projections;

import haxlike.Node;
import haxlike.Projection;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Projection that does not alter the value of the node but
 * prints it on DEBUG level.
 * @param <T> node value class
 */
@Slf4j
@Value
public class InspectProjection<T> implements Projection<T> {
    Projection<T> base;

    @Override
    public Node<T> project(Node<T> node) {
        return base
            .project(node)
            .map(
                value -> {
                    log.debug("[inspect] {}", value);
                    return value;
                }
            );
    }
}
