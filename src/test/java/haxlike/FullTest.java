package haxlike;

import static haxlike.Nodes.*;
import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import java.util.Optional;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FullTest {

    @BeforeAll
    static void setUp() {
        TestUtil.setTraceLogging();
    }

    @AfterAll
    static void tearDown() {
        TestUtil.resetLogging();
    }

    @Test
    void engine_shouldResolveCorrectly() {
        final Engine engine = Engine
            .<Env>builder()
            .withResolver(AllPosts.class, Resolvers::fetchAllPosts)
            .withResolver(PostComments.class, Resolvers::fetchComments)
            .withCommonForkJoinPool()
            .withSelectionStrategy(
                SelectionStrategies
                    .priorityStrategy()
                    .withPriorityHigherThan(AllPosts.class, PostComments.class)
            )
            .build(new Env());

        final Node<List<Comment>> node = asList(new AllPosts())
            .mapEach(Post::getId)
            .flatMapEach(PostComments::of)
            .foldLeft(List::append, List.nil());

        assertThat(engine.resolve(node)).hasSize(6);
    }

    // --- Env
    private static interface CanSimulateDelay {
        void simulateDelay();
    }

    @Value
    public static class Env implements CanSimulateDelay {

        @Override
        public void simulateDelay() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
    }

    // --- Data
    @Value
    @With
    public static class Post {
        int id;
        List<Comment> comments;

        public Optional<List<Comment>> getComments() {
            return Optional.ofNullable(comments);
        }
    }

    @Value
    public static class Comment {
        int postId;
        int userId;
        String text;
    }

    // --- Resolvables
    @Value
    public static class AllPosts implements Resolvable<List<Post>> {}

    @Value(staticConstructor = "of")
    public static class PostComments implements Resolvable<List<Comment>> {
        int postId;
    }

    // --- Resolvers
    private static class Resolvers {

        public static List<Post> fetchAllPosts(
            CanSimulateDelay env,
            AllPosts resolvable
        ) {
            env.simulateDelay();
            return List.range(0, 4).map(i -> new Post(i, null));
        }

        public static List<List<Comment>> fetchComments(
            Env env,
            List<PostComments> rs
        ) {
            env.simulateDelay();
            return rs.map(
                r ->
                    List
                        .range(0, r.postId)
                        .map(i -> new Comment(r.postId, i, "text"))
            );
        }
    }
}
