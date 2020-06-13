name := "tripletail"

val akkaVersion = "2.6.6"
val akkkHttpVersion = "10.1.12"
val typesafeConfVersion = "1.4.0"
val upickleVersion = "1.1.0"
val scalatestVersion = "3.1.2"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.11"
)

lazy val tripletail = project.in(file("."))
  .aggregate(shared, client, server)
  .settings(common)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = (project in file("shared"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "1.1.0",
      "org.scalatest" %% "scalatest" % scalatestVersion % Test
    )
  )

lazy val client = (project in file("client"))
  .dependsOn(shared)
  .enablePlugins(JavaAppPackaging)
  .settings(common)
  .settings(
    maintainer := "tripletail@runbox.com",
    mainClass := Some("tripletail.Client"),
    libraryDependencies ++= {
      val openjfxVersion = "14"
      Seq(
        "org.scalafx" %% "scalafx" % "8.0.192-R14",
        "org.openjfx" % "javafx-controls" % openjfxVersion,
        "org.openjfx" % "javafx-media" % openjfxVersion,
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe" % "config" % typesafeConfVersion,
        "com.lihaoyi" %% "upickle" % upickleVersion
      )
    }
  )

lazy val server = (project in file("server"))
  .dependsOn(shared)
  .enablePlugins(JavaServerAppPackaging)
  .settings(common)
  .settings(
    maintainer := "tripletail@runbox.com",
    mainClass := Some("tripletail.Server"),
    libraryDependencies ++= {
      val quillVersion = "3.5.1"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "de.heikoseeberger" %% "akka-http-upickle" % "1.32.0",
        "com.lihaoyi" %% "upickle" % upickleVersion,
        "io.getquill" %% "quill-sql" % quillVersion,
        "io.getquill" %% "quill-async-postgres" % quillVersion,
        "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
        "org.jodd" % "jodd-mail" % "5.1.4",
        "com.typesafe" % "config" % typesafeConfVersion,
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % Test,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
        "org.scalatest" %% "scalatest" % scalatestVersion % Test
      )
    },
    scalacOptions ++= Seq("-Ywarn-macros:after"),
    javaOptions in Test += "-Dquill.binds.log=true"
  )