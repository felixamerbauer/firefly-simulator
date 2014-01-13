package ui

import java.net.URI

import com.typesafe.scalalogging.slf4j.Logging

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.WindowEvent
import scalafx.application.JFXApp
import scalafx.scene.control.Hyperlink

object Main extends JFXApp with Logging {
  logger.info("starting main")
  val footer = new Hyperlink {
    text = "https://github.com/felixamerbauer/firefly-simulator"
    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) {
        java.awt.Desktop.getDesktop().browse(new URI("https://github.com/felixamerbauer/firefly-simulator"))
      }
    }

  }
  stage = Tabs
  stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler[WindowEvent]() {
    override def handle(event: WindowEvent) {
      Settings.Controller.setDefaultValues
    }
  });
}

