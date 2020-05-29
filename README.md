# haxlike

A Java experiment for declarative data fetching, akin to [haxl][].

[haxl]: https://github.com/facebook/Haxl

## Overview

### Resolvers & Engines

#### Define a Resolvable

A resolvable is a value that implements the `Resolvable<T>` interface. It represents
a deferred value that will eventually yield a result of type `T`:

```java
@Value
public class User implements Resolvable<UserDto> {
  int id;
}
```

By using [lombok][]'s `@Value` annotation, we get necessary equality and hashCode
semantics for free.

[lombok]: https://projectlombok.org/

#### Define an Environment

An environment will be passed to the data fetching logic that we'll define in a
second. It can contain contain everything from configuration values to database
connections:

```java
@Value
public class Env {
  UserService userService;
}
```

#### Define a Resolver

There are multiple types or resolvers, the most interesting being batched ones.
These take the environment we defined and a list of entities to resolve:

```java
public class Resolvers {

  public static Results<User, UserDto> resolveUsers(Env env, List<User> users) {
    final List<Integer> ids = users.map(User::getId);
    final List<UserDto> dtos = env.getUserService().fetchAll(ids);
    return Results.match(users, User::getId, dtos, UserDto::getId);
  }
}
```

A resolver is required to associate every resolvable with its result, which is
aided by matching functions like `Results.match()` above.

#### Create an Engine

An engine brings together an environment, a selection of resolvers, as well as
strategies for batch selection and resolution:

```java
final Engine engine = Engine
    .<Env>builder()
    .withResolver(User.class, Resolvers::resolveUsers)
    .withCommonForkJoinPool()
    .build(new Env(userService));
```

This engine will use the common thread pool to perform parallel resolution
of batches.

By default, resolution is sequential and batches are resolved once they are
available. You can tweak that behaviour using `haxlike.ResolutionStrategy`
and `haxlike.SelectionStrategy`.

### Run it!

The most boring case is resolving a single node:

```java
engine.resolve(new User(1));
// => UserDto(id=1, ...)
```

More than one node will still result in only one query to the `userService`;
and you can apply transformations to single nodes, lists and tuples:

```java
engine.resolve(
    tuple(new User(1), new User(2))
        .map((a, b) -> a.getFriends().append(b.getFriends())));
// => [2,3,4,...]
```

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
