package ui

import scala.collection.mutable.Buffer
import scala.util.Random
import javafx.event.ActionEvent
import javafx.event.EventHandler
import scalafx.Includes.jfxObjectProperty2sfx
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.CategoryAxis
import scalafx.scene.chart.NumberAxis
import scalafx.scene.control.Button
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import ui.MyTab.TResults
import ui.Settings.Controller.ExecutionSettings
import scalafx.scene.chart.BarChart
import ui.Settings._
import com.typesafe.scalalogging.slf4j.Logging
import scalafx.scene.control.Label
import javafx.application.Platform
import algorithm.Factory
import algorithm.Callback
import algorithm.Problem
import algorithm.Termination
import algorithm.Termination._
import algorithm.MySimulation
import scalafx.scene.chart.XYChart
import scalafx.scene.chart.LineChart
import net.sourceforge.cilib.ff.FFA

class MyCallback(settings: ExecutionSettings) extends javafx.concurrent.Task[Unit] with Callback with Logging {
  var stopped = false
  var simulation: MySimulation = _

  def stop { stopped = true }

  override def call {
    simulation = Factory.build(settings, callback = this)
    simulation.run
  }

  private def updateUI(generation: Int, best: Double) {
    logger.debug(s"updateUI $generation $best")
    Platform.runLater(new Runnable() {
      override def run() {
        Execution.Controller.updateProgress(generation, best)
      }
    })
  }

  override def update(generation: Int, best: Double) {
    logger.debug(s"update $generation $best")
    if (settings.visualization) {
      Thread.sleep(settings.visualizationDelay)
    }
    updateUI(generation, best)
  }

  override def start {
    logger.info("start")
  }

  override def end {
    logger.info("end")
    Execution.Controller.stop
  }

}

object Execution extends VBox with Logging {

  object Controller {
    var settings: ExecutionSettings = _
    var callback: MyCallback = _
    def init(settings: ExecutionSettings) {
      this.settings = settings
      this.callback = new MyCallback(settings)
    }

    def updateProgress(generation: Int, best: Double) {
      settings.termination.get match {
        case Generations =>
          val curProgress = generation.toDouble / settings.terminationGenerations
          logger.info(s"curProgress $curProgress ${settings.terminationGenerations} $generation")
          progressBar.progress_=(curProgress)
        case Time =>
          val algorithm = callback.simulation.algorithm
          val stoppingCondition = algorithm.getStoppingConditions().get(0)
          val progress = stoppingCondition.getPercentageCompleted(algorithm)
          progressBar.progress_=(progress)
      }
      series.getData().add(XYChart.Data((generation).toString, best))
    }

    def start {
      startPauseButton.text_=("Pause")
      stopButton.style_=("-fx-base: red")
      stopButton.disable_=(false)
      val th = new Thread(callback)
      th.setDaemon(true)
      th.start
    }

    def pause {
      startPauseButton.text_=("Start")
    }

    def stop {
      startPauseButton.style_=("-fx-base: grey")
      startPauseButton.disable_=(true)
      stopButton.style_=("-fx-base: grey")
      stopButton.disable_=(true)
      resultsButton.style_=("-fx-base: red")
      resultsButton.disable_=(false)
    }

    def results {
      Results.udpate(callback.simulation.bestSolutions)
      Tabs.Controller.switchTo(TResults)
    }
  }

  import Controller._

  // Category/x values
  val generations = (1 to 10).toSeq.map(_.toString)

  val xAxis = new CategoryAxis {
    label = "Generation"
    categories = ObservableBuffer(generations)
  }
  val yAxis = new NumberAxis {
    label = "Fitness"
    tickLabelFormatter = NumberAxis.DefaultFormatter(this, "Value", "")
  }
  val series = new XYChart.Series[String, Number]()

  val startPauseButton = new Button {
    maxWidth = 100
    maxHeight = 100
    text = "Start"
    style = "-fx-base: red"
    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) {
        if (text.value == "Pause") pause else start
      }
    }
  }

  val stopButton = new Button {
    maxWidth = 100
    maxHeight = 100
    text = "Stop"
    disable_=(true)
    style = "-fx-base: grey"
    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) { stop }
    }
  }

  val resultsButton = new Button {
    maxWidth = 100
    maxHeight = 100
    text = "Show Results"
    style = "-fx-base: grey"
    disable_=(true)
    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) { results }
    }
  }

  val controlButtons = new HBox {
    spacing = 10
    content = List(
      startPauseButton,
      stopButton,
      resultsButton)
  }

  val progressBar = new ProgressBar {
    prefWidth_=(Double.MaxValue)
    progress_=(0.0)
  }

  vgrow = Priority.ALWAYS
  hgrow = Priority.ALWAYS
  spacing = 10
  padding = Insets(20)
  content = List(
    // Setup chart
    //    new BarChart(xAxis, yAxis) {
    //      barGap = 1
    //      categoryGap = 2
    //      title = "Best Fitness for each Firefly Generation"
    //      data() += series
    //    },
    new LineChart[String, Number](xAxis, yAxis) {
      //      barGap = 1
      //      categoryGap = 2
      title = "Best Fitness Value for each Firefly Generation"
      data() += series
    },
    separator,
    new Label { text = "Progress" },
    progressBar,
    separator,
    controlButtons)
}