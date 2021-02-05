Akka Http GraphQL
-----------------
>Prototype app using Akka-Http, GraphQL and Sangria.

Test
----
1. sbt clean test

Run
---
1. sbt clean run
2. view graphql client at: http://localhost:7777/
3. in graphql client target: http://localhost:7777/graphql
4. test: { list { id name } }
5. test: { find(id: 1) { name } }