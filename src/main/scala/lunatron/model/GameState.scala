package lunatron.model

import indigo.Seconds
import lunatron.model.snakemodel.SnakeDirection

sealed trait GameState {
  val hasCrashed: Boolean
  val lastSnakeDirection: SnakeDirection
  val lastEkansDirection: SnakeDirection
  def updateNow(time: Seconds, currentSnakeDirection: SnakeDirection, currentEkansDirection: SnakeDirection): GameState
}

object GameState {

  final case class Crashed(
      crashedAt: Seconds,
      snakeLengthOnCrash: Int,
      lastUpdated: Seconds,
      lastSnakeDirection: SnakeDirection,
      lastEkansDirection: SnakeDirection
  ) extends GameState {
    val hasCrashed: Boolean = true

    def updateNow(
        time: Seconds,
        currentSnakeDirection: SnakeDirection,
        currentEkansDirection: SnakeDirection
    ): GameState.Crashed =
      this.copy(
        lastUpdated = time,
        lastSnakeDirection = currentSnakeDirection,
        lastEkansDirection = currentEkansDirection
      )
  }

  final case class Running(
      lastUpdated: Seconds,
      lastSnakeDirection: SnakeDirection,
      lastEkansDirection: SnakeDirection
  ) extends GameState {
    val hasCrashed: Boolean = false

    def updateNow(
        time: Seconds,
        currentSnakeDirection: SnakeDirection,
        currentEkansDirection: SnakeDirection
    ): GameState.Running =
      this.copy(
        lastUpdated = time,
        lastSnakeDirection = currentSnakeDirection,
        lastEkansDirection = currentEkansDirection
      )

    def crash(crashedAt: Seconds, snakeLengthOnCrash: Int): GameState.Crashed =
      GameState.Crashed(crashedAt, snakeLengthOnCrash: Int, lastUpdated, lastSnakeDirection, lastEkansDirection)
  }

  object Running {
    val start: Running = GameState.Running(Seconds.zero, SnakeDirection.Up, SnakeDirection.Down)
  }
}
