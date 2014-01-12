package algorithm

import net.sourceforge.cilib.algorithm.initialisation.ClonedPopulationInitialisationStrategy
import net.sourceforge.cilib.controlparameter.ConstantControlParameter
import net.sourceforge.cilib.controlparameter.LinearlyVaryingControlParameter
import net.sourceforge.cilib.ff.FFA
import net.sourceforge.cilib.ff.firefly.StandardFirefly
import net.sourceforge.cilib.ff.positionupdatestrategies.StandardFireflyPositionUpdateStrategy
import net.sourceforge.cilib.functions.continuous.unconstrained.Spherical
import net.sourceforge.cilib.math.random.generator.seeder.NetworkBasedSeedSelectionStrategy
import net.sourceforge.cilib.math.random.generator.seeder.Seeder
import net.sourceforge.cilib.measurement.generic.Iterations
import net.sourceforge.cilib.problem.FunctionOptimisationProblem
import net.sourceforge.cilib.stoppingcondition.Maximum
import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition
import ui.Settings.Controller.ExecutionSettings
import algorithm.Termination._
import net.sourceforge.cilib.stoppingcondition.StoppingCondition

object Factory {
  def build(settings: ExecutionSettings, callback: Callback): MySimulation = {
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

    val initialisationStrategy = new ClonedPopulationInitialisationStrategy
    initialisationStrategy.setEntityType(firefly)

    val problem = new FunctionOptimisationProblem
    problem.setDomain("R(-5.12:5.12)^30")
    problem.setFunction(new Spherical)

    val stoppingCondition = settings.termination.get match {
      case Generations => new MeasuredStoppingCondition(new Iterations(), new Maximum(), settings.terminationGenerations)
      case Time => ???
      case Quality => ???
    }

    val ffa = new FFA()
    ffa.setInitialisationStrategy(initialisationStrategy)
    ffa.addStoppingCondition(stoppingCondition)

    Seeder.setSeederStrategy(new NetworkBasedSeedSelectionStrategy())
    new MySimulation(algorithm = ffa, problem = problem, callback = callback)

  }
}