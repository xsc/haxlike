# haxlike

A Java experiment for declarative data fetching, akin to [haxl][].

[haxl]: https://github.com/facebook/Haxl

## Declarative Data Fetching

To understand what haxlike does, consider the following code:

```java
var users = fetchUsers()
  .traverse(user -> {
    var posts = fetchPosts(user.getId());
    return new UserWithPosts(user, posts);
  });
```

This is a naïve implementation of attaching a one-to-many relation (posts) to
an entity (users). If these were imperative calls, you'd perform one query to get all
users, then one additonal query for _each and every_ user to get their posts. This
is the so-called _N+1 query problem_ and can get you in terrible trouble.

With haxlike you can write exactly the above piece of code. You'll need one more
line to actually trigger the data fetching, but behind the scenes the engine will
take care of:

- **Batching:** Run queries for group of similar entities instead of one by one.
- **Deduplication:** Don't include the same entity multiple times in a query.
- **Caching**: Don't fetch an entity more than once.

On top of that, haxlike provides a functional style based on immutable data
structures, with useful traversal and manipulation functions for the most
common use cases.

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
