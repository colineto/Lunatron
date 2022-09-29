package lunatron.model.tronmodel

case class Scores(
    snake: Int,
    ekans: Int
) {
  def resetSnake = Scores.resetSnake(this)

  def resetEkans = Scores.resetEkans(this)
}

object Scores {
  def resetSnake(scores: Scores) =
    scores.copy(snake = 0)

  def resetEkans(scores: Scores) =
    scores.copy(ekans = 0)
}
