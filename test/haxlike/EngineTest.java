package haxlike;

import static haxlike.Nodes.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class EngineTest {
    Engine.Instance engine;

    @BeforeEach
    void setUp() {
        engine =
            Engine
                .of(String.class)
                .register(TestResolvable.class, EngineTest::testResolve)
                .register(SlowResolvable.class, EngineTest::slowResolve)
                .build("ENV");
    }

    private static CompletableFuture<List<Integer>> testResolve(
        String env,
        List<TestResolvable> batch
    ) {
        log.info("[resolve] {}", batch);
        return CompletableFuture.completedFuture(
            batch
                .stream()
                .map(TestResolvable::getValue)
                .collect(Collectors.toList())
        );
    }

    private static CompletableFuture<List<Integer>> slowResolve(
        String env,
        List<SlowResolvable> batch
    ) {
        return CompletableFuture.supplyAsync(
            () -> {
                log.info("[resolve] {}", batch);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                return batch
                    .stream()
                    .map(SlowResolvable::getValue)
                    .collect(Collectors.toList());
            }
        );
    }

    @Test
    void resolve_shouldResolveValueNode() {
        Node<Integer> node = value(1);
        Integer expected = 1;

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveMapNode() {
        Node<Integer> node = value(1).map(x -> x + 1);
        Integer expected = 2;

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolvFlatMapNode() {
        Node<Integer> node = value(1).map(x -> x + 1);
        Integer expected = 2;

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveResolvable() {
        Node<Integer> node = new TestResolvable(1).map(x -> x + 1);
        Integer expected = 2;

        assertThat(engine.resolve(node)).isEqualTo(expected);
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

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveWithNode() {
        Node<Integer> node = promise(1)
            .with(slow(2))
            .map((a, b) -> a + b)
            .with(list(promise(3), slow(4)))
            .flatMap((a, b) -> promise(a * b.get(0)));
        Integer expected = 9;

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    // --- Test Resolvable
    @Value
    private static class TestResolvable implements Resolvable<Integer> {
        Integer value;
    }

    @Value
    private static class SlowResolvable implements Resolvable<Integer> {
        Integer value;
    }

    private static Node<Integer> promise(int i) {
        return new TestResolvable(i);
    }

    private static Node<Integer> slow(int i) {
        return new SlowResolvable(i);
    }
}
