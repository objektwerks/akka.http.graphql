Akka Http GraphQL
-----------------
>App using Akka-Http, GraphQL and Sangria.

Test
----
1. sbt clean test

Run
---
1. sbt clean run
2. view graphql client at: http://localhost:7777/
3. via graphql client:
    * target: http://localhost:7777/graphql
    * query: { list { id name } }
    * query: { find(id: 1) { name } }