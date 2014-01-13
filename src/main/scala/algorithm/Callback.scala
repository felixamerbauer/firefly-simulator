package algorithm
/* keep track when something interessting happens in the algorithm */ 
trait Callback {
  /**
   * new generation calculated
   * @param generation current generation
   * @param best best value for current generation
   */
  def update(generation: Int, best: Double): Unit
  def end: Unit
}

object DoNothing extends Callback {
  override def update(generation: Int, best: Double): Unit = {}
  override def end: Unit = {}
}