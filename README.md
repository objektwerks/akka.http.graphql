Akka Http GraphQL
-----------------
>App using Akka-Http, GraphQL and Sangria.

Analysis
--------
>Despite all the ***brouhaha*** surrounding GraphQL, Scala developers will likely find it verbose.

>ScalaJs ***noticeably*** simplifies Json- ***and*** Scala-encoded ***shared messages*** between Json
>clients and Scala servers.

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