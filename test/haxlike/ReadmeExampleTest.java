package haxlike;

import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import lombok.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReadmeExampleTest {

    @BeforeAll
    static void setUp() {
        TestUtil.setTraceLogging();
    }

    @AfterAll
    static void tearDown() {
        TestUtil.resetLogging();
    }

    static final Engine engine = Engine
        .builder()
        .withResolver(Users.class, Users::resolve)
        .withResolver(PostsByUser.class, PostsByUser::resolve)
        .withCommonForkJoinPool()
        .build(null);

    @Test
    void readmeExample_shouldResolveCorrectly() {
        var users = Users
            .fetch()
            .flatMapEach(
                user -> {
                    var posts = PostsByUser.fetch(user.getId());
                    return posts.map(values -> new UserWithPosts(user, values));
                }
            );

        assertThat(engine.resolve(users)).hasSize(4);
    }

    @Test
    void readmeExample_alternative_shouldResolveCorrectly() {
        var users = Users
            .fetch()
            .attachEach(
                UserWithPosts::new,
                user -> PostsByUser.fetch(user.getId())
            );

        assertThat(engine.resolve(users)).hasSize(4);
    }

    // --- Data
    @Value
    public static class Post {
        int id;
    }

    @Value
    public static class User {
        int id;
    }

    @Value
    public static class UserWithPosts {
        User user;
        List<Post> posts;
    }

    // --- Resolvables
    @Value(staticConstructor = "fetch")
    public static class Users implements ListResolvable<User> {

        public static List<User> resolve(Object env, Users resolvable) {
            return List.range(0, 4).map(i -> new User(i));
        }
    }

    @Value(staticConstructor = "fetch")
    public static class PostsByUser implements ListResolvable<Post> {
        int userId;

        public static List<List<Post>> resolve(
            Object env,
            List<PostsByUser> rs
        ) {
            return rs.map(
                r -> List.range(1, r.userId).map(i -> new Post(i * r.userId))
            );
        }
    }
}
