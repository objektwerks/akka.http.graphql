name := "tripletail"

val akkaVersion = "2.6.10"
val akkkHttpVersion = "10.2.1"
val typesafeConfVersion = "1.4.0"
val upickleVersion = "1.2.2"
val scalatestVersion = "3.2.3"

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}
lazy val javaFXModules =
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.13.4"
)

lazy val tripletail = project
  .in(file("."))
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
      "com.lihaoyi" %% "upickle" % upickleVersion,
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
      Seq(
        "org.scalafx" %% "scalafx" % "14-R19",
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe" % "config" % typesafeConfVersion,
        "com.lihaoyi" %% "upickle" % upickleVersion
      )
    }
  )
  .settings(
    libraryDependencies ++= javaFXModules.map(m =>
      "org.openjfx" % s"javafx-$m" % "14.0.1" classifier osName
    )
  )

lazy val server = (project in file("server"))
  .dependsOn(shared)
  .enablePlugins(JavaServerAppPackaging)
  .settings(common)
  .settings(
    maintainer := "tripletail@runbox.com",
    mainClass := Some("tripletail.Server"),
    libraryDependencies ++= {
      val quillVersion = "3.5.3"
      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "de.heikoseeberger" %% "akka-http-upickle" % "1.35.0",
        "com.lihaoyi" %% "upickle" % upickleVersion,
        "io.getquill" %% "quill-sql" % quillVersion,
        "io.getquill" %% "quill-async-postgres" % quillVersion,
        "com.github.cb372" %% "scalacache-caffeine" % "0.28.0",
        "org.jodd" % "jodd-mail" % "6.0.1",
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
