name := "tripletail"

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
  .enablePlugins(JlinkPlugin)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "1.1.0",
      "org.scalatest" %% "scalatest" % "3.1.1" % Test
    ),
    jlinkModules := {
      jlinkModules.value :+ "jdk.unsupported"
    },
    jlinkIgnoreMissingDependency := JlinkIgnore.everything
  )

lazy val client = (project in file("client"))
  .aggregate(shared)
  .dependsOn(shared)
  .enablePlugins(JlinkPlugin)
  .settings(common)
  .settings(
    libraryDependencies ++= {
      val openjfxVersion = "14"
      Seq(
        "org.scalafx" %% "scalafx" % "12.0.2-R18",
        "org.openjfx" % "javafx-controls" % openjfxVersion,
        "org.openjfx" % "javafx-media" % openjfxVersion
      )
    },
    jlinkModules := {
      jlinkModules.value :+ "jdk.unsupported"
    },
    jlinkIgnoreMissingDependency := JlinkIgnore.everything
  )

lazy val server = (project in file("server"))
  .aggregate(shared)
  .dependsOn(shared)
  .enablePlugins(JlinkPlugin)
  .settings(common)
  .settings(
    mainClass := Some("tripletail.Server"),
    libraryDependencies ++= {
      val akkaVersion = "2.6.4"
      val akkkHttpVersion = "10.1.11"
      val quillVersion = "3.5.1"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "de.heikoseeberger" %% "akka-http-upickle" % "1.32.0",
        "com.lihaoyi" %% "upickle" % "1.1.0",
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
    javaOptions in IntegrationTest += "-Dquill.binds.log=true",
    jlinkModules := {
      jlinkModules.value :+ "jdk.unsupported"
    },
    jlinkIgnoreMissingDependency := JlinkIgnore.everything
  )