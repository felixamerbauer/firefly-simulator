package ui

import algorithm.Common.Problems
import algorithm.Common.StringProblemMap
import algorithm.Problem
import algorithm.Termination
import algorithm.Termination.Generations
import algorithm.Termination.Termination
import algorithm.Termination.Time
import algorithm.Termination.stringTerminationMap
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
import scalafx.scene.control.ToggleButton
import scalafx.scene.control.ToggleGroup
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import ui.MyTab.TExecution
import algorithm.Rastrigin

/**
 * Settings tab in GUI
 */
object Settings extends VBox {
  val AlgorithmFirefly = "Firefly"

  /* Handles interaction with user */
  object Controller {
    /**
     * Settings that can be chosen in GUI
     * Default values defined here are preselected in the GUI control elements
     */
    case class ExecutionSettings(
      algorithm: Option[String] = Some(AlgorithmFirefly),
      problem: Option[Problem] = Some(Rastrigin),
      alpha: Double = 0.2d,
      beta: Double = 0.2d,
      gamma: Double = 1.0d,
      population: Int = 20,
      termination: Option[Termination] = Some(Generations),
      terminationGenerations: Int = 10,
      terminationTime: Int = 10,
      visualization: Boolean = true,
      visualizationDelay: Int = 500) {

      /** are all mandatory fields selected? */
      lazy val isValid = Seq(algorithm, problem, termination).forall(_.isDefined)
    }

    /* use the default values for the GUI */
    def setDefaultValues {
      alpha.value_=(settings.alpha)
      beta.value_=(settings.beta)
      gamma.value_=(settings.gamma)
      population.value_=(settings.population)
      problemToggle.head.selected_=(true)
      terminationToggle.head.selected_=(true)
      terminationGenerations.value_=(settings.terminationGenerations)
      terminationTime.value_=(settings.terminationTime)
      visualization.selected_=(settings.visualization)
      visualizationDelay.value_=(settings.visualizationDelay)
    }

    /* current settings */
    private var settings = ExecutionSettings()

    // update methods start
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
    def setPopulation(population: Int) {
      settings = settings.copy(population = population)
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
    def setVisualization(visualization: Boolean) {
      settings = settings.copy(visualization = visualization)
      update
    }
    def setVisualizationDelay(visualizationDelay: Int) {
      settings = settings.copy(visualizationDelay = visualizationDelay)
      update
    }
    // update methods stop

    /* udpate GUI for current settings */
    private def update {
      visualizationDelay.disable_=(!settings.visualization)
      terminationGenerations.disable_=(true)
      terminationTime.disable_=(true)
      settings.termination match {
        case Some(termination) => termination match {
          case Generations =>
            terminationGenerations.disable_=(false)
          case Time =>
            terminationTime.disable_=(false)
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

  /**
   * Helper method to generate toggles
   * @param values selection options
   * @param worker handler function for selection changes
   */
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

  /**
   * Helper method to generate combo boxes
   * @param values selection options
   * @param worker handler function for selection changes
   */
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

  private val visualizationDelay = comboGenerator[Int](Seq(1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setVisualizationDelay)

  private val terminationGenerations = comboGenerator[Int](Seq(2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setTerminationGenerations)
  private val terminationTime = comboGenerator[Int](Seq(1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 1000), setTerminationTime)

  private val alpha = comboGenerator[Double](Seq(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d), setAlpha)
  private val beta = comboGenerator[Double](Seq(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d), setBeta)
  private val gamma = comboGenerator[Double](Seq(0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d), setGamma)
  private val population = comboGenerator[Int](Seq(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100), setPopulation)
  private val terminationToggle = toggleGenerator(Termination.values.toSeq.sortBy(_.id).map(_.toString).toList, setTermination)
  private val problemToggle = toggleGenerator(Problems.map(_.name).toList, setProblem)

  vgrow = Priority.ALWAYS
  hgrow = Priority.ALWAYS
  spacing = 10
  padding = Insets(20)

  // put all UI elements together
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
        new Label { minWidth = 100; text = "Population"; alignment_=(Pos.BASELINE_RIGHT) },
        population,
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
        terminationTime)
    },
    separator,
    new Label { text = "Visualization" },
    new HBox {
      spacing = 10
      content = List(
        visualization,
        new Label { minWidth = 100; text = "Delay ms"; alignment_=(Pos.BASELINE_RIGHT) },
        visualizationDelay)
    },
    separator,
    execution)
}