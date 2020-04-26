name := "tripletail"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.11"
)

lazy val shared = (project in file("shared"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "1.0.0",
      "org.scalatest" %% "scalatest" % "3.1.1" % Test
    )
  )

lazy val server = (project in file("server"))
  .aggregate(shared)
  .settings(common)
  .settings(
    libraryDependencies ++= {
      val akkaVersion = "2.6.4"
      val akkkHttpVersion = "10.1.11"
      val quillVersion = "3.5.1"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe.akka" %% "akka-agent" % "2.5.31",
        "de.heikoseeberger" %% "akka-http-upickle" % "1.32.0",
        "io.getquill" %% "quill-sql" % quillVersion,
        "io.getquill" %% "quill-async-postgres" % quillVersion,
        "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
        "org.jodd" % "jodd-mail" % "5.1.4",
        "com.typesafe" % "config" % "1.4.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % Test,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
        "org.scalatest" %% "scalatest" % "3.1.1" % Test
      )
    },
    scalacOptions ++= Seq("-Ywarn-macros:after"),
    javaOptions in IntegrationTest += "-Dquill.binds.log=true"
  ).dependsOn(shared)