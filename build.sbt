name := "akka.http.graphql"
organization := "objektwerks"
version := "0.1-SNAPSHOT"
scalaVersion := "2.13.8"
libraryDependencies ++= {
  val akkaVersion = "2.6.18"
  val akkkHttpVersion = "10.2.9"
  val quillVersion = "3.10.0"  // 3.11 contains dev.zio
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkkHttpVersion,
    "net.virtual-void" %%  "json-lenses" % "0.6.2",
    "org.sangria-graphql" %% "sangria" % "2.1.3",
    "org.sangria-graphql" %% "sangria-spray-json" % "1.0.2",
    "io.getquill" %% "quill-sql" % quillVersion,
    "io.getquill" %% "quill-jdbc" % quillVersion,
    "com.h2database" % "h2" % "2.1.210",
    "com.typesafe" % "config" % "1.4.1",
    "ch.qos.logback" % "logback-classic" % "1.2.10",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % Test,
    "org.scalatest" %% "scalatest" % "3.2.10" % Test
  )
}
