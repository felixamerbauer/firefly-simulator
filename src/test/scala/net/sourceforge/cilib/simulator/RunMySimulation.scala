package net.sourceforge.cilib.simulator

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
import algorithm.MySimulation
import algorithm.DoNothing

object RunMySimulation extends App {

  val alpha = new LinearlyVaryingControlParameter(0.2d, 0.0d)

  val betaMin = new ConstantControlParameter()
  betaMin.setParameter(0.2d)

  val gamma = new ConstantControlParameter()
  gamma.setParameter(0.2d)

  val positionUpdateStrategy = new StandardFireflyPositionUpdateStrategy
  positionUpdateStrategy.setAlpha(alpha)
  positionUpdateStrategy.setBetaMin(betaMin)
  positionUpdateStrategy.setGamma(gamma)

  val firefly = new StandardFirefly
  firefly.setPositionUpdateStrategy(positionUpdateStrategy)

  val initialisationStrategy = new ClonedPopulationInitialisationStrategy
  initialisationStrategy.setEntityType(firefly)

  val problem = new FunctionOptimisationProblem
  problem.setDomain("R(-5.12:5.12)^30")
  problem.setFunction(new Spherical)

  val stoppingCondition = new MeasuredStoppingCondition(new Iterations(), new Maximum(), 5)

  val ffa = new FFA()
  ffa.setInitialisationStrategy(initialisationStrategy)
  ffa.addStoppingCondition(stoppingCondition)

  Seeder.setSeederStrategy(new NetworkBasedSeedSelectionStrategy())
  val simulation = new MySimulation(algorithm = ffa, problem = problem, callback = DoNothing)

  simulation.run
}