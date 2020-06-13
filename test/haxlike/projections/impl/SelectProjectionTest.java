package haxlike.projections.impl;

import static haxlike.projections.Projection.select;
import static org.assertj.core.api.Assertions.*;

import fj.data.List;
import haxlike.Engine;
import haxlike.Node;
import haxlike.TestUtil;
import haxlike.projections.Projection;
import haxlike.relations.Relation;
import haxlike.resolvers.Resolver;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.*;

public class SelectProjectionTest {

    // --- DTOs
    @Value
    @Builder
    @With
    private static class UserDto {
        int id;
        int friendId;
        UserDto friend;

        public static final Relation<UserDto, UserDto> FRIEND = User.relation(
            UserDto::withFriend,
            UserDto::getFriendId
        );

        public static UserDto create(int id) {
            return UserDto.builder().id(id).friendId(id + 3).build();
        }
    }

    // --- Resolvers
    public static final Resolver<Object, Integer, UserDto> User = Resolver.declare(
        "User",
        (List<Integer> ids) -> ids.map(UserDto::create)
    );

    // --- Engine
    public static final Engine engine = Engine
        .builder()
        .withResolver(User)
        .build(null);

    // --- Test
    @BeforeAll
    static void setUp() {
        TestUtil.setTraceLogging();
    }

    @AfterAll
    static void tearDown() {
        TestUtil.resetLogging();
    }

    @Test
    void select_shouldAttachRelation() {
        // given
        final Node<UserDto> node = User.fetch(1);
        final Projection<UserDto> project = select(UserDto.FRIEND);

        // when
        final UserDto user = engine.resolve(node.project(project));

        // then
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getFriend().getId()).isEqualTo(4);
        assertThat(user.getFriend().getFriend()).isNull();
    }

    @Test
    void select_shouldAttachRelationAndProjection() {
        // given
        final Node<UserDto> node = User.fetch(1);
        final Projection<UserDto> projection = select(
            UserDto.FRIEND,
            select(UserDto.FRIEND)
        );

        // when
        final UserDto user = engine.resolve(node.project(projection));

        // then
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getFriend().getId()).isEqualTo(4);
        assertThat(user.getFriend().getFriend().getId()).isEqualTo(7);
        assertThat(user.getFriend().getFriend().getFriend()).isNull();
    }
}
