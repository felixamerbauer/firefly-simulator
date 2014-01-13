package algorithm

import scala.collection.mutable.Buffer

import com.typesafe.scalalogging.slf4j.Logging

import net.sourceforge.cilib.algorithm.AbstractAlgorithm
import net.sourceforge.cilib.algorithm.AlgorithmEvent
import net.sourceforge.cilib.algorithm.AlgorithmListener
import net.sourceforge.cilib.ff.FFA
import net.sourceforge.cilib.math.random.generator.Rand
import net.sourceforge.cilib.problem.{Problem => CilibProblem}

/**
 * Runs a CIlib firefly algorithm and keeps track of the best and current results
 * @param algorithm works currently only with firefly
 * @param problem work the optimization problem
 * @param callback allows other componentes (e.g. GUI) to be notified if something happens
 */
class MySimulation(val algorithm: FFA, val problem: CilibProblem, callback: Callback) extends AlgorithmListener with Logging {

  // keep track of the best solution of each generation
  val bestSolutions = Buffer[Double]()
  
  /* fired just prior to the execution of the main loop of the algorithm */
  override def algorithmStarted(event: AlgorithmEvent) {
    logger.debug(s"algorithmStarted")
  }

  /* fired when the algorithm has completed normally. */
  override def algorithmFinished(event: AlgorithmEvent) {
    logger.debug(s"algorithmFinished")
    callback.end
  }

  /* fired after each iteration of the mail loop of the algorithm. */
  override def iterationCompleted(event: AlgorithmEvent) {
    val algorithm = event.getSource()
    val bestSolutionFitness = algorithm.getBestSolution().getFitness().getValue()
    val iterations = algorithm.getIterations()
    logger.debug(s"iterationCompleted $iterations/$bestSolutionFitness")
    bestSolutions += bestSolutionFitness
    callback.update(generation = iterations, best = bestSolutionFitness)
  }

  /* starts executiong the algorithm */
  def run {
    Rand.reset()
    val alg: AbstractAlgorithm = algorithm.asInstanceOf[AbstractAlgorithm]
    alg.addAlgorithmListener(this)
    alg.setOptimisationProblem(problem)
    alg.performInitialisation()
    algorithm.run()
  }

  /* not required */
  override def getClone = ???

}