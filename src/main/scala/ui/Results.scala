package ui

import com.typesafe.scalalogging.slf4j.Logging

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.control.TextArea
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox

/**
 * Results tab showing results of run as CSV
 */
object Results extends VBox with Logging {
  /**
   * convert list of results to pretty CSV
   */
  def init(results: Seq[Double]) {
    val data = for ((result, idx) <- results.zipWithIndex) yield ((idx + 1) + ";" + "%.4f".format(result))
    val header = "Generation;Fitness Value\n"
    val csv = data.mkString(header, "\n", "")
    textArea.text_=(csv)
  }

  val textArea = new TextArea {
    text = ""
    prefHeight_=(600)
  }
  vgrow = Priority.ALWAYS
  hgrow = Priority.ALWAYS
  spacing = 10
  padding = Insets(20)
  content = List(
    separator,
    textArea,
    separator)
}