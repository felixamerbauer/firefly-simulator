package ui

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.control.TextArea
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import com.typesafe.scalalogging.slf4j.Logging

object Results extends VBox with Logging {
  def udpate(results: Seq[Double]) {
    logger.debug(s"update $results")
    val data = for ((result, idx) <- results.zipWithIndex) yield ((idx + 1) + "\t" + "%.4f".format(result))
    val header = "Generation\tFitness Value\n"
    val csv = data.mkString(header, "\n", "")
    textArea.text_=(csv)
  }

  val textArea = new TextArea {
    text = ""
    prefHeight_=(500)
  }
  vgrow = Priority.ALWAYS
  hgrow = Priority.ALWAYS
  spacing = 10
  padding = Insets(20)
  content = List(
    new Label { text = "Results" },
    separator,
    textArea,
    separator)
}