package ui

import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.geometry.Side
import scalafx.scene.Scene
import scalafx.scene.control.Tab
import scalafx.scene.control.Tab.sfxTab2jfx
import scalafx.scene.control.TabPane
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.scene.control.TabPane.sfxTabPane2jfx
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import ui.Main.footer
import com.typesafe.scalalogging.slf4j.Logging

/* Keeps track of all tabs */
object MyTab extends Enumeration {
  type MyTab = Value
  val TSettings, TExecution, TResults = Value
}
import ui.MyTab._

/** GUI container for all tabs **/
object Tabs extends JFXApp.PrimaryStage {

  private val tabs: Seq[(Tab, Int)] = Seq(settings, execution, results).zipWithIndex

  /* handle user interaction */
  object Controller extends Logging {
    /**
     *  switch to another tab, disable all others
     *  @param tab new tab
     */
    def switchTo(tab: MyTab) {
      tabPane.getSelectionModel().select(tab.id)
      for (i <- 0 until tabs.size) {
        tabPane.tabs.get(i).setDisable(i != tab.id)
      }
    }
    
    /**
     * Enable and switch to other tab but keep all other currently enabled tabs enabled
     * @param tab new tab
     */
    def enable(tab: MyTab) {
      tabPane.tabs.get(tab.id).setDisable(false)
      tabPane.getSelectionModel().select(tab.id)
    }
  }
  private val settings = new Tab {
    text = "Settings"
    content = Settings
  }
  private val execution = new Tab {
    text = "Execution"
    content = Execution
    disable_=(true)
  }
  private val results = new Tab {
    text = "Results"
    content = Results
    disable_=(true)
  }

  private val tabPane = new TabPane {
    minWidth = 400
    tabs = Seq(
      settings, execution, results)
    tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
    side = Side.TOP
  }
  title = "Firefly Algorithm"
  width = 1000
  height = 700
  scene = new Scene {
    root = new VBox {
      vgrow = Priority.ALWAYS
      hgrow = Priority.ALWAYS
      spacing = 10
      padding = Insets(20)
      content = List(tabPane, footer)
    }
  }
}