name := "akka.http.graphql"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.16"
libraryDependencies ++= {
  val akkaVersion = "2.6.21" // Don't upgrade due to BUSL 1.1!
  val akkkHttpVersion = "10.2.10" // Don't upgrade due to BUSL 1.1!
  val quillVersion = "3.10.0"  // 3.11 contains dev.zio
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkkHttpVersion,
    "net.virtual-void" %%  "json-lenses" % "0.6.2",
    "org.sangria-graphql" %% "sangria" % "4.0.2",
    "org.sangria-graphql" %% "sangria-spray-json" % "1.0.3",
    "io.getquill" %% "quill-sql" % quillVersion,
    "io.getquill" %% "quill-jdbc" % quillVersion,
    "com.h2database" % "h2" % "2.3.232",
    "com.typesafe" % "config" % "1.4.3",
    "ch.qos.logback" % "logback-classic" % "1.5.16",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % Test,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
}
