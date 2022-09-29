package lunatron.model

import indigo.shared.constants.Key
import lunatron.model.snakemodel.{Snake, SnakeDirection}

sealed trait ControlScheme {
  def swap: ControlScheme =
    this match {
      case ControlScheme.Turning(_, _) =>
        ControlScheme.directedKeys

      case ControlScheme.Directed(_, _, _, _) =>
        ControlScheme.turningKeys

      case ControlScheme.WASDTurning(_, _) =>
        ControlScheme.directedKeys

      case ControlScheme.WASDDirected(_, _, _, _) =>
        ControlScheme.turningKeys
    }
}

object ControlScheme {
  final case class Turning(left: Key, right: Key) extends ControlScheme
  final case class WASDTurning(left: Key, right: Key) extends ControlScheme
  final case class Directed(up: Key, down: Key, left: Key, right: Key) extends ControlScheme
  final case class WASDDirected(up: Key, down: Key, left: Key, right: Key) extends ControlScheme

  val turningKeys: Turning = Turning(Key.LEFT_ARROW, Key.RIGHT_ARROW)
  val wasdTurningKeys: WASDTurning = WASDTurning(Key.KEY_A, Key.KEY_D)
  val directedKeys: Directed = Directed(Key.UP_ARROW, Key.DOWN_ARROW, Key.LEFT_ARROW, Key.RIGHT_ARROW)
  val wasdDirectedKeys: WASDDirected = WASDDirected(Key.KEY_W, Key.KEY_S, Key.KEY_A, Key.KEY_D)
}
