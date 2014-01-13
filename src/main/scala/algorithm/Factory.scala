package algorithm

import algorithm.Termination.Generations
import algorithm.Termination.Time
import net.sourceforge.cilib.algorithm.initialisation.ClonedPopulationInitialisationStrategy
import net.sourceforge.cilib.controlparameter.ConstantControlParameter
import net.sourceforge.cilib.controlparameter.LinearlyVaryingControlParameter
import net.sourceforge.cilib.ff.FFA
import net.sourceforge.cilib.ff.firefly.StandardFirefly
import net.sourceforge.cilib.ff.positionupdatestrategies.StandardFireflyPositionUpdateStrategy
import net.sourceforge.cilib.math.random.generator.seeder.NetworkBasedSeedSelectionStrategy
import net.sourceforge.cilib.math.random.generator.seeder.Seeder
import net.sourceforge.cilib.measurement.generic.Iterations
import net.sourceforge.cilib.problem.FunctionOptimisationProblem
import net.sourceforge.cilib.stoppingcondition.Maximum
import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition
import ui.Settings.Controller.ExecutionSettings
import net.sourceforge.cilib.`type`.types.container.Vector

/**
 * Configures CIlib standard firefly algorithm
 * @param setttings settings from GUI
 * @param callback callback that gets passed on to simulation
 */
object Factory {
  def build(settings: ExecutionSettings, callback: Callback): MySimulation = {
    // alpha, beta, gamma
    val cpAlpha = new LinearlyVaryingControlParameter(settings.alpha, 0.0d)

    val cpBetaMin = new ConstantControlParameter()
    cpBetaMin.setParameter(settings.beta)

    val cpGamma = new ConstantControlParameter()
    cpGamma.setParameter(settings.gamma)

    val positionUpdateStrategy = new StandardFireflyPositionUpdateStrategy
    positionUpdateStrategy.setAlpha(cpAlpha)
    positionUpdateStrategy.setBetaMin(cpBetaMin)
    positionUpdateStrategy.setGamma(cpGamma)

    val firefly = new StandardFirefly
    firefly.setPositionUpdateStrategy(positionUpdateStrategy)

    // population initialisation
    val initialisationStrategy = new ClonedPopulationInitialisationStrategy
    initialisationStrategy.setEntityType(firefly)

    // choose problem via reflection from class name
    val problem = new FunctionOptimisationProblem
    problem.setDomain(settings.problem.get.domain)
    val functionClass = Class.forName(settings.problem.get.className)
    val instance = functionClass.newInstance().asInstanceOf[fj.F[Vector, _ <: Number]]
    problem.setFunction(instance)

    // stopping condition
    val stoppingCondition = settings.termination.get match {
      case Generations => new MeasuredStoppingCondition(new Iterations(), new Maximum(), settings.terminationGenerations)
      case Time => new TimeStoppingCondition(settings.terminationTime)
    }

    // complete whole firefly algorithm
    val ffa = new FFA()
    ffa.setInitialisationStrategy(initialisationStrategy)
    ffa.addStoppingCondition(stoppingCondition)

    Seeder.setSeederStrategy(new NetworkBasedSeedSelectionStrategy())
    new MySimulation(algorithm = ffa, problem = problem, callback = callback)

  }
}