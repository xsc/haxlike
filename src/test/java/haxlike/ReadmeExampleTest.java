package haxlike;

import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import haxlike.resolvers.*;
import lombok.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReadmeExampleTest {
    private static final Engine engine = Engine
        .builder()
        .withResolver(Users.class, Users::resolve)
        .withResolver(PostsByUser.class, PostsByUser::resolve)
        .withCommonForkJoinPool()
        .build(null);

    @BeforeAll
    static void setUp() {
        TestUtil.setTraceLogging();
    }

    @AfterAll
    static void tearDown() {
        TestUtil.resetLogging();
    }

    // --- Fixtures
    private static UserWithPosts expectedUser(int id) {
        return new UserWithPosts(new User(id), expectedPosts(id));
    }

    private static List<Post> expectedPosts(int id) {
        return List.range(1, id).map(i -> new Post(i * id));
    }

    // --- Tests
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

        assertThat(engine.resolve(users))
            .containsExactly(
                expectedUser(0),
                expectedUser(1),
                expectedUser(2),
                expectedUser(3)
            );
    }

    @Test
    void readmeExample_alternative_shouldResolveCorrectly() {
        var users = Users
            .fetch()
            .attachEach(
                UserWithPosts::new,
                user -> PostsByUser.fetch(user.getId())
            );

        assertThat(engine.resolve(users))
            .containsExactly(
                expectedUser(0),
                expectedUser(1),
                expectedUser(2),
                expectedUser(3)
            );
    }

    @Test
    void readmeExample_shouldResolveCorrectly_2() {
        // 1. Create Resolver
        var User = Resolver.declare(
            "User",
            (List<Integer> userIds) -> {
                var results = userIds.map(User::new);
                return Results.zip(userIds, results);
            }
        );

        // 2. Create Engine
        var engine = Engine.builder().withResolver(User).build(null);

        // 3. Resolve users
        var users = engine.resolve(Nodes.list(User.fetch(1), User.fetch(2)));

        // 4. Verify
        assertThat(users).hasSize(2);
        assertThat(users).extracting("id").containsExactly(1, 2);
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
    public static class Users implements Resolvable.ListResolvable<User> {

        public static List<User> resolve(Users resolvable) {
            return List.range(0, 4).map(i -> new User(i));
        }
    }

    @Value(staticConstructor = "fetch")
    public static class PostsByUser implements Resolvable.ListResolvable<Post> {
        int userId;

        public static List<List<Post>> resolve(List<PostsByUser> rs) {
            return rs.map(r -> expectedPosts(r.userId));
        }
    }
}
