package lunatron.model.tronmodel

import lunatron.model.snakemodel.CollisionCheckOutcome

sealed trait CollisionCheckResult {
  val result: CollisionCheckOutcome
}
object CollisionCheckResult {
  final case class BothNoCollision(snake: CollisionCheckOutcome.NoCollision, ekans: CollisionCheckOutcome.NoCollision)
      extends CollisionCheckResult { val result = snake }
  final case class SnakePickUp(result: CollisionCheckOutcome.PickUp) extends CollisionCheckResult
  final case class EkansPickUp(result: CollisionCheckOutcome.PickUp) extends CollisionCheckResult
  final case class SnakeCrashed(result: CollisionCheckOutcome.Crashed) extends CollisionCheckResult
  final case class EkansCrashed(result: CollisionCheckOutcome.Crashed) extends CollisionCheckResult

  def fromCollisionCheckOutcomes(snakeCollision: CollisionCheckOutcome, ekansCollision: CollisionCheckOutcome) =
    (snakeCollision, ekansCollision) match {
      case (s: CollisionCheckOutcome.NoCollision, e: CollisionCheckOutcome.NoCollision) => BothNoCollision(s, e)
      case (s: CollisionCheckOutcome.Crashed, _)                                        => SnakeCrashed(s)
      case (_, e: CollisionCheckOutcome.Crashed)                                        => EkansCrashed(e)
      case (s: CollisionCheckOutcome.PickUp, _)                                         => SnakePickUp(s)
      case (_, e: CollisionCheckOutcome.PickUp)                                         => EkansPickUp(e)
    }
}
