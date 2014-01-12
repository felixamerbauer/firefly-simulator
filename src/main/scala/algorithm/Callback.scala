package algorithm

trait Callback {
  def start: Unit
  def update(generation: Int, best: Double): Unit
  def end: Unit
}

object DoNothing extends Callback {
  override def start: Unit = {}
  override def update(generation: Int, best: Double): Unit = {}
  override def end: Unit = {}
}