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
import scalafx.scene.chart.XYChart
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

//class MyCallback(settings: ExecutionSettings) extends javafx.concurrent.Task[Unit] with Callback with Logging {
//  var stopped = false
//
//  def stop { stopped = true }
//
//  override def call {
//    val maxGeneration = if (settings.termination.exists(_ == TerminationGenerations)) settings.terminationGenerations else Int.MaxValue
//    val firefly = new Firefly(
//      maxGeneration = maxGeneration,
//      alpha = settings.alpha,
//      betamin = settings.beta,
//      gamma = settings.gamma,
//      fitnessFunction = settings.fitnessFunction.get,
//      callback = this,
//      // TODO
//      seed = None)
//    firefly.run
//  }
//  
//  private def updateUI(generation: Int, best: Double){
//    Platform.runLater(new Runnable() {
//      override def run() {
//        Execution.Controller.updateProgress(generation, best)
//      }
//    })
//  }
//
//  override def continue(generation: Int, best: Double): Boolean = {
//    logger.info(s"continue $generation $best")
//    updateUI(generation, best)
//    val terminationCheck = !isCancelled() && !stopped && (settings.termination.get match {
//      case TerminationGenerations =>
//        logger.debug(s"Checking generations $generation < ${settings.terminationGenerations}")
//        generation < settings.terminationGenerations
//      case TerminationTime => false
//      case TerminationQuality =>
//        false
//    })
//    logger.info("Current best " + best)
//    // TODO
//    if (!terminationCheck) {
//      Execution.Controller.stop
//    } else {
//      Thread.sleep(1000)
//    }
//    terminationCheck
//  }
//
//  override def end(generation: Int, best: Double) {
//    logger.info("end")
//    updateUI(generation, best)
//    Execution.Controller.stop
//  }
//
//}

object Execution extends VBox with Logging {

  object Controller {
    var settings: ExecutionSettings = _
//    var callback: MyCallback = _
    def init(settings: ExecutionSettings) {
      this.settings = settings
//      this.callback = new MyCallback(settings)
    }

    def updateProgress(generation: Int, best: Double) {
      settings.termination.get match {

        case TerminationGenerations =>
          val curProgress = generation.toDouble / settings.terminationGenerations
          logger.info(s"curProgress $curProgress ${settings.terminationGenerations} $generation")
          progressBar.progress_=(curProgress)
        case TerminationTime => ???
        case TerminationQuality => ???
      }
    }

    def start {
      startPauseButton.text_=("Pause")
      stopButton.style_=("-fx-base: red")
      stopButton.disable_=(false)
//      val th = new Thread(callback)
//      th.setDaemon(true)
//      th.start
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
      Tabs.Controller.switchTo(TResults)
    }
  }

  import Controller._

  // Category/x values
  val chromosomes = (1 to 50).toSeq.map(_.toString)

  val xAxis = new CategoryAxis {
    label = "Chromosome"
    categories = ObservableBuffer(chromosomes)
  }
  val yAxis = new NumberAxis {
    label = "Fitness"
    tickLabelFormatter = NumberAxis.DefaultFormatter(this, "Value", "")
  }
  // Assign data using a helper function
  def xyData(ys: Seq[Number]) = ObservableBuffer(chromosomes zip ys map (xy => XYChart.Data(xy._1, xy._2)))
  val fitnessValues: Buffer[Number] = Buffer()
  chromosomes.foreach { _ =>
    fitnessValues += Random.nextInt(1000)
  }
  val series = XYChart.Series("Population", xyData(fitnessValues))

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
    new BarChart(xAxis, yAxis) {
      barGap = 1
      categoryGap = 2
      title = "Fitness for each Chromosome"
      data() += series
    },
    separator,
    new Label { text = "Progress" },
    progressBar,
    separator,
    controlButtons)
}