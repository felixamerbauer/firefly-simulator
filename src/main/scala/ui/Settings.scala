package ui

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.{ ToggleButton => JfxToggleBtn }
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.control.CheckBox
import scalafx.scene.control.ComboBox
import scalafx.scene.control.Label
import scalafx.scene.control.Separator
import scalafx.scene.control.ToggleButton
import scalafx.scene.control.ToggleGroup
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import ui.MyTab._
import algorithm.Problem
import algorithm.Rastrigin
import algorithm.Termination
import algorithm.Common._
import algorithm.Termination._

object Settings extends VBox {
  val AlgorithmFirefly = "Firefly"

  object Controller {
    case class ExecutionSettings(
      algorithm: Option[String] = Some(AlgorithmFirefly),
      problem: Option[Problem] = Some(Rastrigin),
      alpha: Double = 0.2d,
      beta: Double = 0.2d,
      gamma: Double = 1.0d,
      termination: Option[Termination] = Some(Generations),
      terminationGenerations: Int = 10,
      terminationTime: Int = 1,
      terminationQuality: Int = 1,
      visualization: Boolean = true,
      visualizationDelay: Int = 1000) {

      lazy val isValid = Seq(algorithm, problem, termination).forall(_.isDefined)
    }

    def setDefaultValues {
      alpha.value_=(settings.alpha)
      beta.value_=(settings.beta)
      gamma.value_=(settings.gamma)
      problemToggle.head.selected_=(true)
      terminationToggle.head.selected_=(true)
      terminationGenerations.value_=(settings.terminationGenerations)
      terminationTime.value_=(settings.terminationTime)
      terminationQuality.value_=(settings.terminationQuality)
      visualization.selected_=(settings.visualization)
      visualizationDelay.value_=(settings.visualizationDelay)
    }

    private var settings = ExecutionSettings()
    def setProblem(problem: Option[String]) {
      settings = settings.copy(problem = problem map StringProblemMap)
      update
    }
    def setAlgorithm(algorithm: Option[String]) {
      settings = settings.copy(algorithm = algorithm)
      update
    }
    def setAlpha(alpha: Double) {
      settings = settings.copy(alpha = alpha)
      update
    }
    def setBeta(beta: Double) {
      settings = settings.copy(beta = beta)
      update
    }
    def setGamma(gamma: Double) {
      settings = settings.copy(gamma = gamma)
      update
    }
    def setTermination(termination: Option[String]) {
      settings = settings.copy(termination = termination map stringTerminationMap)
      update
    }
    def setTerminationGenerations(terminationGenerations: Int) {
      settings = settings.copy(terminationGenerations = terminationGenerations)
      update
    }
    def setTerminationTime(terminationTime: Int) {
      settings = settings.copy(terminationTime = terminationTime)
      update
    }
    def setTerminationQuality(terminationQuality: Int) {
      settings = settings.copy(terminationQuality = terminationQuality)
      update
    }
    def setVisualization(visualization: Boolean) {
      settings = settings.copy(visualization = visualization)
      update
    }
    def setVisualizationDelay(visualizationDelay: Int) {
      settings = settings.copy(visualizationDelay = visualizationDelay)
      update
    }
    private def update {
      println(s"update $settings")
      visualizationDelay.disable_=(!settings.visualization)
      terminationGenerations.disable_=(true)
      terminationQuality.disable_=(true)
      terminationTime.disable_=(true)
      settings.termination match {
        case Some(termination) => termination match {
          case Generations =>
            terminationGenerations.disable_=(false)
          case Time =>
            terminationTime.disable_=(false)
          case Quality =>
            terminationQuality.disable_=(false)
        }
        case None =>
      }
      if (settings.isValid) {
        execution.disable_=(false)
        execution.style_=("-fx-base: red")
      } else {
        execution.disable_=(true)
        execution.style_=("-fx-base: grey")
      }
    }
    def curSettings = settings
  }

  import Controller._

  private def toggleGenerator(values: List[String], worker: Option[String] => Unit): List[ToggleButton] = {
    // Radio Button Toggle Group
    val toggleLabel = new Label {
      text = ""
      style = "-fx-font-size: 2em;"
    }
    val tog = new ToggleGroup {
      selectedToggle.onChange(
        (_, _, newValue) => newValue match {
          case btn: JfxToggleBtn => worker(Some(btn.getText))
          case _ => worker(None)
        })
    }
    val firstSelected = values.length == 1
    values.map { e =>
      new ToggleButton {
        minWidth = 100
        text = e
        selected_=(firstSelected)
        toggleGroup = tog
      }
    }

  }

  private def comboGenerator[T](values: Seq[T], worker: T => Unit) = new ComboBox[T] {
    minWidth = 100
    maxWidth = 100
    promptText = "Choose..."
    items = ObservableBuffer(values)

    value.onChange((_, _, newValue) => {
      worker(newValue)
    })
  }

  private val visualization = new CheckBox {
    maxWidth_=(100)
    minWidth_=(100)
    text = "Enable"

    selected.onChange((_, _, newValue) => {
      setVisualization(newValue)
    })
  }

  private val execution = new Button {
    maxWidth = 100
    maxHeight = 100
    text = "Execution"
    style = "-fx-base: grey"
    disable_=(true)

    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent) {
        Execution.Controller.init(curSettings)
        Tabs.Controller.switchTo(TExecution)
      }
    }
  }

  private val visualizationDelay = comboGenerator[Int](Seq(0, 1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setVisualizationDelay)

  private val terminationGenerations = comboGenerator[Int](Seq(2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setTerminationGenerations)
  private val terminationTime = comboGenerator[Int](Seq(1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setTerminationTime)
  private val terminationQuality = comboGenerator[Int](Seq(1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setTerminationQuality)

  private val alpha = comboGenerator[Double](Seq(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d), setAlpha)
  private val beta = comboGenerator[Double](Seq(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d), setBeta)
  private val gamma = comboGenerator[Double](Seq(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d), setGamma)
  private val terminationToggle = toggleGenerator(Termination.values.toSeq.sortBy(_.id).map(_.toString).toList, setTermination)
  private val problemToggle = toggleGenerator(Problems.map(_.name).toList, setProblem)

  vgrow = Priority.ALWAYS
  hgrow = Priority.ALWAYS
  spacing = 10
  padding = Insets(20)
  content = List(
    separator,
    new Label { text = "Algorithm" },
    new HBox {
      spacing = 10
      content = toggleGenerator(List(AlgorithmFirefly), setAlgorithm)
    },
    separator,
    new Label { text = "Problem" },
    new HBox {
      spacing = 10
      content = problemToggle
    },
    separator,
    new Label { text = "Algorithm Parameters" },
    new HBox {
      spacing = 10
      content = List(
        new Label { minWidth = 100; text = "Alpha"; alignment_=(Pos.BASELINE_RIGHT) },
        alpha,
        new Label { minWidth = 100; text = "Beta"; alignment_=(Pos.BASELINE_RIGHT) },
        beta,
        new Label { minWidth = 100; text = "Gamma"; alignment_=(Pos.BASELINE_RIGHT) },
        gamma)
    },
    separator,
    new Label { text = "Termination Conditon" },
    new HBox {
      spacing = 10
      content = terminationToggle
    },
    new HBox {
      spacing = 10
      content = List(
        terminationGenerations,
        terminationTime,
        terminationQuality)
    },
    separator,
    new Label { text = "Visualization" },
    new HBox {
      spacing = 10
      content = List(
        visualization,
        new Label { minWidth = 100; text = "Delay"; alignment_=(Pos.BASELINE_RIGHT) },
        visualizationDelay)
    },
    separator,
    execution)
}