# haxlike

A Java experiment for declarative data fetching, akin to [haxl][].

[haxl]: https://github.com/facebook/Haxl

## Features

There are a few things that can make data fetching more efficient:

- **Batching:** Run queries for group of similar entities instead of one by one.
- **Parallelism:** Run independent queries in parallel instead of sequentially.
- **Deduplication:** Don't include the same entity multiple times in a single query.
- **Caching:** Don't fetch an entity more than once.

Adding these often comes at significant cost, either in pure time spent on
implementation, code readability or verbosity. [haxl][] and its companions allow
you to get rid of that overhead.

An alternative to the approach employed by this library is the one inherent to
[dataloader][], which is a good fit if you prefer a more imperative look and
feel over a functional one.

[dataloader]: https://github.com/graphql/dataloader

## Quickstart

```java
import haxlike.*;
import haxlike.resolvers.*;
```

### Engine

To allow for data fetching operations you need to create a so-called _engine_ that
encapsulates all resolution logic. It allows you to customise a series of things:

- **Environment**: What dependencies are available to me?
- **Resolvers**: How do I fetch a specific set of entities?
- **Resolution strategies**: How do I parallelise multiple data fetching operations?
- **Selection strategies**: What entities do I resolve next?

The default strategy is to sequentially resolve everything as soon as it's available.

```java
// 1. Declare Resolver
var User = Resolver.declare("User", (Env env, List<Integer> userIds) -> {
    var results = env.getDatabase().fetchUsers(userIds);
    return Results.match(userIds, results, UserDto::getId);
});

// 2. Create Engine
var engine = Engine.<Env>builder()
    .withResolver(User)
    .withCommonForkJoinPool()
    .build(new Env(database));
```

### Nodes

Declarative data fetching allows you to build up a tree of transformations (inner nodes)
and data fetching operations (leaves). The engine will then analyse that tree and use
its resolution and selection strategies to decide on the next step.

There are three types of nodes: value nodes, list nodes and tuples:

```java
var firstUser  = User.fetch(1);
var secondUser = User.fetch(2);
var users      = Nodes.list(firstUser, secondUser);
var userTuple  = Nodes.tuple(firstUser, secondUser);
```

They differ in the transformation functions they offer. List nodes, for example, allow
you to apply a function to each element, and tuple nodes provide a multi-parameter
transformation. Let's concatenate the user names of two users, to illustrate:

```java
var usernames = Nodes.tuple(firstUser, secondUser)
    .map((a, b) -> a.getUserName() + ", " + b.getUsername());
```

Note that no data fetching has occured yet - you're only just building up the tree.

### Resolution

Once you have created your node, you can pass it to the engine:

```java
var userDto = engine.resolve(firstUser);
```

And you can be sure that even complex data fetching will now be performed efficiently,
no matter how naive you tackle the implementation:

```java
var usersWithPosts = AllUsers.fetch()
    .flatMapEach(user ->
        UserPosts
            .fetch(user.getId())
            .map(posts -> new UserWithPosts(user, posts)));
engine.resolve(usersWithPosts);
```

But this is just the beginning - haxlike offers some powerful abstractions that will
make your code even more declarative.

## License

```
MIT License

Copyright (c) 2020 Yannick Scherer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
