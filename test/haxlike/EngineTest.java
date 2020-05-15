package haxlike;

import static haxlike.Nodes.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
import org.junit.jupiter.api.Test;

public class EngineTest {

    @Test
    void resolve_shouldResolveValueNode() {
        Node<Integer> node = value(1);
        Integer expected = 1;

        assertThat(Engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveMapNode() {
        Node<Integer> node = value(1).map(x -> x + 1);
        Integer expected = 2;

        assertThat(Engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolvFlatMapNode() {
        Node<Integer> node = value(1).map(x -> x + 1);
        Integer expected = 2;

        assertThat(Engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveResolvable() {
        Node<Integer> node = new TestResolvable(1).map(x -> x + 1);
        Integer expected = 2;

        assertThat(Engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveList() {
        Node<List<Integer>> node = list(
            new TestResolvable(1).map(x -> x + 1),
            value(2),
            new TestResolvable(3),
            new TestResolvable(3)
        );
        List<Integer> expected = List.of(2, 2, 3, 3);

        assertThat(Engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveWithNode() {
        Node<Integer> node = value(1)
            .with(promise(2))
            .map((a, b) -> a + b)
            .with(value(3))
            .flatMap((a, b) -> promise(a * b));
        Integer expected = 9;

        assertThat(Engine.resolve(node)).isEqualTo(expected);
    }

    // --- Test Resolvable
    @Value
    private static class TestResolvable
        implements Resolvable<Integer, TestResolvable> {
        Integer value;

        @Override
        public List<Integer> resolveAll(List<TestResolvable> batch) {
            System.out.printf("resolving %d TestResolvables%n", batch.size());
            return batch
                .stream()
                .map(TestResolvable::getValue)
                .collect(Collectors.toList());
        }
    }

    private static Node<Integer> promise(int i) {
        return new TestResolvable(i);
    }
}
