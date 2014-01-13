package algorithm

import com.typesafe.scalalogging.slf4j.Logging

import net.sourceforge.cilib.algorithm.Algorithm
import net.sourceforge.cilib.stoppingcondition.StoppingCondition

/**
 * Stops after a give duration
 * @param seconds duration in seconds
 */
class TimeStoppingCondition(seconds: Int) extends StoppingCondition[Algorithm] with Logging {
  // remember start and stop for progress calculation
  private val start = System.currentTimeMillis()
  private val stop = start + (seconds * 1000)
  
  override def getClone(): TimeStoppingCondition = new TimeStoppingCondition(seconds)

  override def getPercentageCompleted(algorithm: Algorithm): Double = (System.currentTimeMillis - start) / 1000.d / seconds

  override def apply(algorithm: Algorithm): Boolean = System.currentTimeMillis >= stop
}