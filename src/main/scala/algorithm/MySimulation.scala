package algorithm

import net.sourceforge.cilib.algorithm.AlgorithmListener
import net.sourceforge.cilib.algorithm.AlgorithmEvent
import com.typesafe.scalalogging.slf4j.Logging
import net.sourceforge.cilib.math.random.generator.Rand
import net.sourceforge.cilib.algorithm.AbstractAlgorithm
import net.sourceforge.cilib.algorithm.Algorithm
import net.sourceforge.cilib.problem.Problem
import scala.collection.JavaConversions._

class MySimulation(algorithm: Algorithm, problem: Problem) extends AlgorithmListener with Runnable with Logging {
  /**
   * This event is fired just prior to the execution of the main loop of the algorithm.
   * @param e an event containing a reference to the source algorithm.
   */
  override def algorithmStarted(event: AlgorithmEvent) {
    logger.debug(s"algorithmStarted $event")
  }

  /**
   * This event is fired when the algorithm has completed normally.
   * @param e an event containing a reference to the source algorithm.
   */
  override def algorithmFinished(event: AlgorithmEvent) {
    logger.debug(s"algorithmFinished $event")
  }

  /**
   * This event is fired after each iteration of the mail loop of the algorithm.
   * @param e an event containing a reference to the source algorithm.
   */
  override def iterationCompleted(event: AlgorithmEvent) {
    logger.debug(s"iterationCompleted $event")
    val algorithm = event.getSource()
    val bestSolution = algorithm.getBestSolution()
    val fitness = bestSolution.getFitness().getValue()
    logger.debug(s"fitness $fitness")
//    val position = bestSolution.getPosition()
//    logger.debug(s"position $position")
    logger.debug(s"size ${algorithm.getSolutions().size}")
    for(solution <- algorithm.getSolutions()) {
      logger.debug(s"Solution $solution")
    }
    //    .getBestSolution()
  }

  override def run {
    Rand.reset()
    val alg: AbstractAlgorithm = algorithm.asInstanceOf[AbstractAlgorithm]
    alg.addAlgorithmListener(this)
    alg.setOptimisationProblem(problem)
    alg.performInitialisation()
    algorithm.run()
  }

  override def getClone = ???

}