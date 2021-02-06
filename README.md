Akka Http GraphQL
-----------------
>App using Akka-Http, GraphQL and Sangria.

Todo
----
1. Convert UserStore to use Quill and H2.

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
    * query: { find(id: 1) { name } }