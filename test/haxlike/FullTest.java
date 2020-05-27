package haxlike;

import static haxlike.Nodes.*;
import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import lombok.Value;
import org.junit.jupiter.api.Test;

public class FullTest {

    @Test
    void engine_shouldResolveCorrectly() {
        final Engine engine = EngineBuilder
            .of(Env.class)
            .withSingleResolvable(AllPosts.class)
            .withResolver(PostComments.class, FullTest::fetchComments)
            .withCommonForkJoinPool()
            .withSelectionStrategy(
                SelectionStrategies
                    .priorityStrategy()
                    .withPriorityLowerThan(AllPosts.class, PostComments.class)
            )
            .build(new Env());

        final Node<List<Comment>> node = asList(new AllPosts())
            .collect(Post::getId)
            .traverse(PostComments::of)
            .foldLeft(List::append, List.nil());

        final Node<List<Comment>> n = tuple(node, new PostComments(7))
            .map((a, b) -> a);

        assertThat(engine.resolve(n)).hasSize(6);
    }

    // --- Env
    @Value
    public static class Env {

        void simulateDelay() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }

    // --- Data
    @Value
    public static class Post {
        int id;
    }

    @Value
    public static class Comment {
        int postId;
        int userId;
        String text;
    }

    // --- Resolvables
    @Value
    public static class AllPosts
        implements Resolvable.Single<Env, List<Post>, AllPosts> {

        @Override
        public List<Post> resolve(Env env, AllPosts resolvable) {
            env.simulateDelay();
            return List.range(0, 4).map(i -> new Post(i));
        }
    }

    @Value(staticConstructor = "of")
    public static class PostComments implements Resolvable<List<Comment>> {
        int postId;
    }

    // --- Resolvers
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
