package ui

import com.typesafe.scalalogging.slf4j.Logging
import scalafx.application.JFXApp
import scalafx.scene.control.Hyperlink
import javafx.stage.WindowEvent
import javafx.event.EventHandler

object Main extends JFXApp with Logging {
  object Controller {

  }
  logger.info("starting main")
  val footer = new Hyperlink {
    text = "https://github.com/felixamerbauer/evolutionary-algorithm"
  }
  stage = Tabs
  stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler[WindowEvent]() {
    override def handle(event:WindowEvent ) {
        Settings.Controller.setDefaultValues
    }
});
}

