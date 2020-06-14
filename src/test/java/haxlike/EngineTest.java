package haxlike;

import static haxlike.Nodes.*;
import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import haxlike.resolvers.Provider;
import haxlike.resolvers.Resolver;
import haxlike.resolvers.Results;
import lombok.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EngineTest {
    Engine engine;

    public static final Resolver<Object, Integer, Integer> TestValue = Resolver.declare(
        "Resolver",
        (List<Integer> values) -> values
    );

    public static final Resolver<Object, Integer, Integer> TestValueSingle = Resolver.declare(
        "Single",
        (Integer value) -> value
    );

    public static final Provider<Object, Integer> TestList = Provider.declare(
        "Provider",
        env -> 1
    );

    @BeforeAll
    static void setUpAll() {
        TestUtil.setTraceLogging();
    }

    @AfterAll
    static void tearDown() {
        TestUtil.resetLogging();
    }

    @BeforeEach
    void setUp() {
        engine =
            Engine
                .<String>builder()
                .withResolver(TestResolvable.class, EngineTest::testResolve)
                .withResolver(SlowResolvable.class, EngineTest::slowResolve)
                .withResolver(TestValue)
                .withResolver(TestValueSingle)
                .withResolver(TestList)
                .withCommonForkJoinPool()
                .withSelectionStrategy(SelectionStrategies.maxStrategy(3))
                .build("ENV");
    }

    private static Results<TestResolvable, Integer> testResolve(
        String env,
        List<TestResolvable> batch
    ) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        return Results.zip(batch, batch.map(TestResolvable::getValue));
    }

    private static List<Integer> slowResolve(
        String env,
        List<SlowResolvable> batch
    ) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        return batch.map(SlowResolvable::getValue);
    }

    @Test
    void resolve_shouldResolveValueNode() {
        Node<Integer> node = value(1);
        Integer expected = 1;

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveDeclared() {
        Node<Integer> node = Nodes
            .list(
                TestList.fetch(),
                TestValue.fetch(0),
                TestValue.fetch(-1),
                TestList.fetch(),
                TestValueSingle.fetch(-1),
                TestValue.fetch(5)
            )
            .foldLeft((a, b) -> a + b, 0);
        Integer expected = 5;

        assertThat(TestList.provide(null)).isEqualTo(1);

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
        List<Integer> expected = List.arrayList(2, 2, 3, 3);

        assertThat(engine.resolve(node)).isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveWithNode() {
        Node<Integer> product = tuple(promise(1), slow(12), promise(3))
            .map((a, b, c) -> a * c);

        Node<Integer> node = promise(1)
            .map((a, b) -> a + b, product)
            .map((a, b) -> a * b, product)
            .flatMap(EngineTest::slow);

        Integer expected = 12;
        assertThat(engine.resolve(node, EngineCaches.defaultCache()))
            .isEqualTo(expected);
    }

    @Test
    void resolve_shouldResolveTraverse() {
        Node<Integer> node = list(slow(1), slow(2))
            .flatMapEach(EngineTest::promise)
            .foldLeft((a, b) -> a + b, 0);
        Integer expected = 3;

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
