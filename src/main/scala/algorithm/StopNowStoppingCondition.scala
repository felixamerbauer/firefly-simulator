package algorithm

import com.typesafe.scalalogging.slf4j.Logging

import net.sourceforge.cilib.algorithm.Algorithm
import net.sourceforge.cilib.stoppingcondition.StoppingCondition

object StopNowStoppingCondition extends StoppingCondition[Algorithm] with Logging {
  override def getClone() = this

  override def getPercentageCompleted(algorithm: Algorithm): Double = 1

  override def apply(algorithm: Algorithm): Boolean = true
}