package tripletail

import akka.actor.ActorSystem

import com.typesafe.config.ConfigFactory

import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.VBox

object Client extends JFXApp {
  val conf = ConfigFactory.load("client.conf")
  implicit val system = ActorSystem.create(conf.getString("server.name"), conf)
  implicit val executor = system.dispatcher
  val logger = system.log
  val serverProxy = ServerProxy(system, executor)
  val url = conf.getString("server.url")
  val api = conf.getString("server.api")

  val appPane = new VBox {
    maxWidth = 600
    maxHeight = 600
    spacing = 6
    padding = Insets(6)
    children = List()
  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Tripletail"
    scene = new Scene {
      root = appPane
    }
  }
}