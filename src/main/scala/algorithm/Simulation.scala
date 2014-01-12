package algorithm

import com.typesafe.scalalogging.slf4j.Logging
import net.sourceforge.cilib.algorithm.AbstractAlgorithm
import net.sourceforge.cilib.algorithm.Algorithm
import net.sourceforge.cilib.algorithm.AlgorithmEvent
import net.sourceforge.cilib.algorithm.AlgorithmListener
import net.sourceforge.cilib.math.random.generator.Rand
import net.sourceforge.cilib.problem.Problem
import scala.collection.mutable.Buffer

class MySimulation(val algorithm: Algorithm, val problem: Problem, callback: Callback) extends AlgorithmListener with Logging {
  val bestSolutions = Buffer[Double]()
  /**
   * This event is fired just prior to the execution of the main loop of the algorithm.
   * @param e an event containing a reference to the source algorithm.
   */
  override def algorithmStarted(event: AlgorithmEvent) {
    logger.debug(s"algorithmStarted")
    callback.start
  }

  /**
   * This event is fired when the algorithm has completed normally.
   * @param e an event containing a reference to the source algorithm.
   */
  override def algorithmFinished(event: AlgorithmEvent) {
    logger.debug(s"algorithmFinished")
    callback.end
  }

  /**
   * This event is fired after each iteration of the mail loop of the algorithm.
   * @param e an event containing a reference to the source algorithm.
   */
  override def iterationCompleted(event: AlgorithmEvent) {
    val algorithm = event.getSource()
    val bestSolutionFitness = algorithm.getBestSolution().getFitness().getValue()
    val iterations = algorithm.getIterations()
    logger.debug(s"iterationCompleted $iterations/$bestSolutionFitness")
    logger.debug(callback.getClass().getCanonicalName())
    bestSolutions += bestSolutionFitness
    callback.update(generation = iterations, best = bestSolutionFitness)
  }

  def run {
    Rand.reset()
    val alg: AbstractAlgorithm = algorithm.asInstanceOf[AbstractAlgorithm]
    alg.addAlgorithmListener(this)
    alg.setOptimisationProblem(problem)
    alg.performInitialisation()
    algorithm.run()
  }

  override def getClone = ???

}