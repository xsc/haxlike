package haxlike;

import static haxlike.projections.Projection.*;
import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import haxlike.relations.Relation;
import haxlike.resolvers.ListProvider;
import haxlike.resolvers.ListResolver;
import haxlike.resolvers.Resolver;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparisonTest {

    private static void log(String fmt, Object... args) {
        final Logger log = LoggerFactory.getLogger(ComparisonTest.class);
        log.info(fmt, args);
    }

    // --- Data Model
    @Value
    public static class User {
        int id;
        String username;

        @With
        List<Post> likedPosts;

        public static final Relation<User, List<Post>> LIKED_POSTS = LikedPostsByUserId.relation(
            User::withLikedPosts,
            User::getId
        );
    }

    @Value
    public static class Post implements Node.Data<Post> {
        int id;
        int userId;
        String text;

        @With
        User author;

        public static final Relation<Post, User> AUTHOR = UserById.relation(
            Post::withAuthor,
            Post::getUserId
        );
    }

    // --- Data Fetching
    private static List<Integer> fetchAllUserIds() {
        log("Fetching all user IDs ...");
        delay();
        return List.arrayList(1, 2, 3);
    }

    private static User fetchUser(int id) {
        log("Fetching user: {}", id);
        delay();
        return new User(id, "User" + id, null);
    }

    private static List<Post> fetchLikedPosts(int id) {
        log("Fetching posts for: {}", id);
        delay();
        return List
            .range(1, id)
            .map(postId -> new Post(postId, id, "Post" + postId, null));
    }

    private static void delay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final ListProvider<Object, Integer> AllUserIds = ListProvider.declare(
        "AllUserIds",
        ComparisonTest::fetchAllUserIds
    );

    private static final Resolver<Object, Integer, User> UserById = Resolver.declare(
        "User",
        ComparisonTest::fetchUser
    );

    private static final ListResolver<Object, Integer, Post> LikedPostsByUserId = ListResolver.declare(
        "PostsByUser",
        ComparisonTest::fetchLikedPosts
    );

    // --- Expected Result
    private static final List<User> EXPECTED_USERS = List
        .arrayList(1, 2, 3)
        .map(
            userId ->
                new User(
                    userId,
                    "User" + userId,
                    List
                        .range(1, userId)
                        .map(
                            postId ->
                                new Post(
                                    postId,
                                    userId,
                                    "Post" + postId,
                                    new User(userId, "User" + userId, null)
                                )
                        )
                )
        );

    // --- Fetch all Users with their liked posts (including post author)
    @Test
    void manualDataFetching_shouldReturnCorrectResult() {
        final List<User> users = fetchAllUserIds()
            .map(
                id -> {
                    var user = fetchUser(id);
                    var posts = fetchLikedPosts(id)
                        .map(
                            post -> {
                                var author = fetchUser(post.getUserId());
                                return post.withAuthor(author);
                            }
                        );
                    return user.withLikedPosts(posts);
                }
            );

        assertThat(users).containsExactlyElementsOf(EXPECTED_USERS);
    }

    @Test
    void dataResolution_shouldReturnCorrectResult() {
        var engine = Engine
            .builder()
            .withResolver(AllUserIds)
            .withResolver(UserById)
            .withResolver(LikedPostsByUserId)
            .withCommonForkJoinPool()
            .build(null);

        var node = AllUserIds
            .fetch()
            .flatMapEach(
                id -> {
                    var user = UserById.fetch(id);
                    var posts = LikedPostsByUserId
                        .fetch(id)
                        .flatMapEach(
                            post -> {
                                var author = UserById.fetch(post.getUserId());
                                return post.map(Post::withAuthor, author);
                            }
                        );
                    return user.map(User::withLikedPosts, posts);
                }
            );

        final List<User> users = engine.resolve(node);

        assertThat(users).containsExactlyElementsOf(EXPECTED_USERS);
    }

    @Test
    void declarativeDataResolution_shouldReturnCorrectResult() {
        var engine = Engine
            .builder()
            .withResolver(AllUserIds)
            .withResolver(UserById)
            .withResolver(LikedPostsByUserId)
            .withCommonForkJoinPool()
            .withTraceLogging()
            .build(null);

        var fullUser = selectList(User.LIKED_POSTS, select(Post.AUTHOR));
        var allUsers = AllUserIds.fetch().flatMapEach(UserById::fetch);

        final List<User> users = engine.resolve(allUsers, fullUser.list());

        assertThat(users).containsExactlyElementsOf(EXPECTED_USERS);
    }
}
