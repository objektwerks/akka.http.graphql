Akka Http GraphQL
-----------------
>App using Akka-Http, GraphQL, Sangria, Quill and H2.

Test
----
1. sbt clean test

Run
---
1. sbt clean run
2. view graphql client at: http://localhost:7777/
3. within graphql client:
   * target: http://localhost:7777/graphql
   * query: { list { id name } }
   * response: {"data":{"list":[{"id":1,"name":"Fred Flintstone"},{"id":2,"name":"Barney Rebel"}]}}
   * query: { find(id: 1) { name } }
   * response: {"data":{"find":{"name":"Fred Flintstone"}}}