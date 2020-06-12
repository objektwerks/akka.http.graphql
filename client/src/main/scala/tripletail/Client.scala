package tripletail

import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.VBox

object Client extends JFXApp {
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