package net.sourceforge.cilib.simulator

import java.io.File

object RunBasicShell extends App {
  val simulators = SimulatorShell.prepare(new File("xml/firefly.xml"))
  SimulatorShell.execute(simulators, new ProgressText(simulators.size))

}