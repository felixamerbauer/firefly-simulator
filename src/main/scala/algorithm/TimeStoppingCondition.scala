package algorithm

import net.sourceforge.cilib.stoppingcondition.StoppingCondition
import net.sourceforge.cilib.algorithm.Algorithm
import com.typesafe.scalalogging.slf4j.Logging

class TimeStoppingCondition(seconds: Int) extends StoppingCondition[Algorithm] with Logging {
  private val start = System.currentTimeMillis()
  private val stop = start + (seconds * 1000)
  override def getClone(): TimeStoppingCondition = new TimeStoppingCondition(seconds)

  override def getPercentageCompleted(algorithm: Algorithm): Double = (System.currentTimeMillis - start) / 1000.d / seconds

  override def apply(algorithm: Algorithm): Boolean = System.currentTimeMillis >= stop
}