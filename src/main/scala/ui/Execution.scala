package ui

import com.typesafe.scalalogging.slf4j.Logging

import algorithm.Callback
import algorithm.Factory
import algorithm.MySimulation
import algorithm.StopNowStoppingCondition
import algorithm.Termination.Generations
import algorithm.Termination.Time
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import scalafx.Includes.jfxObjectProperty2sfx
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.CategoryAxis
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.XYChart
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import ui.MyTab.TResults
import ui.Settings.Controller.ExecutionSettings

class MyCallback(settings: ExecutionSettings) extends javafx.concurrent.Task[Unit] with Callback with Logging {
  private var stopped = false
  var simulation: MySimulation = _

  def stop { stopped = true }

  override def call {
    simulation = Factory.build(settings, callback = this)
    simulation.run
  }

  override def update(generation: Int, best: Double) {
    // Update UI (progress bar and chart)
    Platform.runLater(new Runnable() {
      override def run() {
        Execution.Controller.updateProgress(generation, best)
      }
    })
    if (stopped) {
    	simulation.algorithm.addStoppingCondition(StopNowStoppingCondition)
    } else {
      // sleep if there is a delay between each visualization
      if (settings.visualization) {
        Thread.sleep(settings.visualizationDelay)
      }
    }
  }

  override def start { /*do nothing*/ }

  override def end {
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
          progressBar.progress_=(curProgress)
        case Time =>
          val algorithm = callback.simulation.algorithm
          val stoppingCondition = algorithm.getStoppingConditions().get(0)
          val progress = stoppingCondition.getPercentageCompleted(algorithm)
          progressBar.progress_=(progress)
      }
      if (settings.visualization) {
        series.getData().add(XYChart.Data((generation).toString, best))
      }
    }

    def start {
      startButton.disable_=(true)
      stopButton.style_=("-fx-base: red")
      stopButton.disable_=(false)
      val th = new Thread(callback)
      th.setDaemon(true)
      th.start
    }

    def stop {
      callback.stop
      startButton.style_=("-fx-base: grey")
      stopButton.style_=("-fx-base: grey")
      stopButton.disable_=(true)
      resultsButton.style_=("-fx-base: red")
      resultsButton.disable_=(false)
    }

    def results {
      resultsButton.disable_=(true)
      Results.udpate(callback.simulation.bestSolutions)
      Tabs.Controller.enable(TResults)
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

  val startButton = new Button {
    maxWidth = 100
    maxHeight = 100
    text = "Start"
    style = "-fx-base: red"
    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) { start }
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
      startButton,
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
    new LineChart[String, Number](xAxis, yAxis) {
      title = "Best Fitness Value for each Firefly Generation"
      data() += series
    },
    separator,
    new Label { text = "Progress" },
    progressBar,
    separator,
    controlButtons)
}