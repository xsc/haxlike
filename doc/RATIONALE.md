# Rationale

Consider the following functionality:

```java
fetchUsers(userIds)
    .map(user -> {
        final List<UserDto> friends = fetchUsers(user.getFriends());
        return new UserWithFriends(user, friends);
    });
```

This piece of code, as simple as it looks, has potential for optimisation.
Let's improve it!

## Batching

First up, **batching**. We're doing one query per element in `userIds` to
fetch their friends - and this could just as well be one query over all
the friend IDs:

```java
final List<UserDto> users     = fetchUsers(userIds);
final List<Integer> friendIds = users.bind(UserDto::getFriends);
final List<UserDto> friends   = fetchUsers(friendIds);
```

Of course, now we have to assemble `UserWithFriends` which gets a bit more
intricate:

```java
final HashMap<Integer, UserDto> friendById =
    HashMap.iterableHashMap(friends.map(UserDto::getId).zip(friends));

users.map(user -> {
    final List<UserDto> friends = user.getFriends().map(id -> friendById.get(id).some());
    return new UserWithFriends(user, friends);
});
```

We had to create a lookup table and then manually put every fetched `UserDto` to the
right place. It's not terrible but seems a bit hard.

## Deduplication

Next up, **deduplication**. We might be passing the same ID multiple times to `fetchUsers`
when querying the friends. This is not necessarily a big problem depending on the datastore
but it's definitely a few unnecessary bytes that are sent over the wire. Easily solved,
though, let's dedupe:

```java
final HashSet<Integer> uniqueUserIds = HashSet.iterableHashSet(userIds);
final List<UserDto> users            = fetchUsers(uniqueUserIds.toList());
final HashSet<Integer> friendIds     = HashSet.iterableHashSet(users.bind(UserDto::getFriends));
final List<UserDto> friends          = fetchUsers(friendIds.toList());
...
```

Admittedly, this won't look as bad once we extract a generic `dedupe` function that
handles conversion to a set and back. This exercise is left to the reader.

## Caching

Finally, **caching**. When fetching the initial set of users (`fetchUsers(userIds)`) there
is a chance that we retrieved data that's then fetched again when querying the friends. Best
case, it's a waste of bandwidth; worst case, the second fetch returns different data than
the first one! For the same entity!

Still, easy to address:

```java
final HashSet<Integer> uniqueUserIds = HashSet.iterableHashSet(userIds);
final List<UserDto> users            = fetchUsers(uniqueUserIds.toList());
final HashSet<Integer> friendIds     = HashSet.iterableHashSet(users.bind(UserDto::getFriends));
uniqueUserIds.forEach(friendIds::delete);
final List<UserDto> friends          = fetchUsers(friendIds.toList());
```

## Full Example

The following piece of code is the "optimised" variant of the simple functionality
initially presented:

```java
final HashSet<Integer> uniqueUserIds = HashSet.iterableHashSet(userIds);
final List<UserDto> users            = fetchUsers(uniqueUserIds.toList());
final HashSet<Integer> friendIds     = HashSet.iterableHashSet(users.bind(UserDto::getFriends));
uniqueUserIds.forEach(friendIds::delete);
final List<UserDto> friends          = fetchUsers(friendIds.toList());
final HashMap<Integer, UserDto> friendById =
    HashMap.iterableHashMap(friends.map(UserDto::getId).zip(friends));

users.map(user -> {
    final List<UserDto> friends = user.getFriends().map(id -> friendById.get(id).some());
    return new UserWithFriends(user, friends);
});
```

There is so much noise in there because of our optimisation attempts. It's hard
to understand what the code is doing, and even a cleaned up version does not
improve the situation significantly:

```java
final List<UserDto> users                  = fetchUsers(dedupe(userIds));
final HashSet<Integer> friendIds           = users.bind(UserDto::getFriends);
final List<UserDto> friends                = fetchUsers(dedupe(remove(friendIds, userIds)));
final HashMap<Integer, UserDto> friendById = groupById(friends);

users.map(user -> {
    final List<UserDto> friends = findFriends(friendById, user.getFriends());
    return new UserWithFriends(user, friends);
});
```

## What now?

Here's he thing: There is no need for manual optimisation! If we step away from
imperative code and represent the fetching and transformation as a data structure,
an external engine can inspect it and decide on the best way of producing a
result. This is what haxl and haxlike do!

```java
final Node<List<UserDto>> users = value(userIds).traverse(FetchUser::new);
final Node<List<UserWithFriends>> node =
    users.traverse(user -> {
        final Node<List<UserDto>> friends = value(user.getFriends()).traverse(FetchUser::new);
        return friends.map(xs -> new UserWithFriends(user, xs));
    });

engine.resolve(node);
```

The above is already very close to our initial example, basically identical
once we extract `value(ids).traverse(FetchUser::new)` into a `fetchUsers`
function.

However, it has an unnecessary notion of ordering (even though the underlying engine
will not care about that): First we resolve all the users, then we resolve all the
friends.

The following is equivalent, and arguably easier to understand because it only
concerns itself with exactly one user:

```java
public Node<UserWithFriends> fetchUserWithFriends(int id) {
    return new FetchUser(id).map(user ->
        value(user.getFriends())
            .traverse(FetchUser::new)
            .map(friends -> new UserWithFriends(user, friends)));
}
// ...
engine.resolve(
    value(userIds)
        .traverse(this::fetchUserWithFriends));
```

And there we are: Straightforward code that will be executed efficiently
by haxlike's engine. The best of both worlds!
