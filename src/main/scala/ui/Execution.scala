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
import scalafx.scene.text.Font
import scalafx.scene.effect.DropShadow
import scalafx.geometry.Pos
import javafx.scene.text.FontWeight

/**
 * Keeps track of the algorithm execution.
 * Runs in a separate thread to keep UI responsive.
 *  
 * @param settings settings to configure algorithm
 */
class MyCallback(settings: ExecutionSettings) extends javafx.concurrent.Task[Unit] with Callback with Logging {
  // remember if stop happened
  private var stopped = false
  // link to simulation executing the algorithm
  var simulation: MySimulation = _

  /* call to stop algorithm at next opportunity */
  def stop { stopped = true }

  /* starts the execution */
  override def call {
    simulation = Factory.build(settings, callback = this)
    simulation.run
  }

  /* new generation calculated */
  override def update(generation: Int, best: Double) {
    // Update UI (progress bar and chart)
    Platform.runLater(new Runnable() {
      override def run() {
        Execution.Controller.updateProgress(generation, best)
      }
    })
    // if stop was triggered, tell the algorihm to stop immediately
    if (stopped) {
      simulation.algorithm.addStoppingCondition(StopNowStoppingCondition)
    } else {
      // sleep if there is a delay between each visualization
      if (settings.visualization) {
        Thread.sleep(settings.visualizationDelay)
      }
    }
  }

  /* algorithm finished -> udpate GUI */
  override def end {
    Execution.Controller.stop
  }

}

/* Execution Tab in GUI */
object Execution extends VBox with Logging {

  /* Controller handles interaction with user */
  object Controller {
    var settings: ExecutionSettings = _
    var callback: MyCallback = _
    
    /* initialize GUI according to current settings */
    def init(settings: ExecutionSettings) {
      this.settings = settings
      this.callback = new MyCallback(settings)
      val progressInfo = settings.termination.get match {
        case Generations => s"${settings.terminationGenerations} generations"
        case Time => s"${settings.terminationTime} seconds"
      }
      progressLabel.text_=(s"Progress for problem ${settings.problem.get} and termination after $progressInfo")
      series.setName(s"${settings.population} / ${settings.alpha} / ${settings.beta} / ${settings.gamma}")
    }

    /* update GUI after each new generation */
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

    /* start button pressed -> update GUI and start algorithm in background thread */
    def start {
      startButton.disable_=(true)
      stopButton.style_=("-fx-base: red")
      stopButton.disable_=(false)
      val th = new Thread(callback)
      th.setDaemon(true)
      th.start
    }

    /* stop button pressed -> inform algorithm and update GUI */
    def stop {
      callback.stop
      startButton.style_=("-fx-base: grey")
      stopButton.style_=("-fx-base: grey")
      stopButton.disable_=(true)
      resultsButton.style_=("-fx-base: red")
      resultsButton.disable_=(false)
    }

    /* result button pressed -> enable new tab */
    def results {
      resultsButton.disable_=(true)
      Results.init(callback.simulation.bestSolutions)
      Tabs.Controller.enable(TResults)
    }
  }

  import Controller._

  // Category/x values
  val generations = (1 to 10).toSeq.map(_.toString)

  // x axis in chart
  val xAxis = new CategoryAxis {
    label = "Generation"
    categories = ObservableBuffer(generations)
  }
  // y axis in chart
  val yAxis = new NumberAxis {
    label = "Fitness"
    tickLabelFormatter = NumberAxis.DefaultFormatter(this, "Value", "")
  }
  // data to display in chart
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
  val header = new Label {
    text = "Best Fitness Value for each Firefly Generation"
    font = Font("Verdana", FontWeight.BOLD, 20)
    effect = new DropShadow()
  }
  val progressLabel = new Label {
    font = Font("Verdana", FontWeight.BOLD, 12)
  }
  
  // settings for whole UI element
  vgrow = Priority.ALWAYS
  hgrow = Priority.ALWAYS
  spacing = 10
  padding = Insets(20)
  alignment_=(Pos.CENTER)
  
  // put all GUI elements together
  content = List(
    header,
    separator,
    new LineChart[String, Number](xAxis, yAxis) {
      data() += series
    },
    separator,
    progressLabel,
    progressBar,
    separator,
    controlButtons)
}