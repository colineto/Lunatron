package lunatron.model.tronmodel

import indigoextras.geometry.{BoundingBox, Vertex}
import lunatron.model.snakemodel.{CollisionCheckOutcome, Snake, SnakeDirection, SnakeStatus}

final case class Tron(snake: Snake, ekans: Snake) {
  def update(
      gridSize: BoundingBox,
      snakeCollisionCheck: Vertex => CollisionCheckOutcome,
      ekansCollisionCheck: Vertex => CollisionCheckOutcome
  ): (Tron, CollisionCheckResult) =
    Tron.update(this, gridSize, snakeCollisionCheck, ekansCollisionCheck)

  def growSnake = Tron.growSnake(this)

  def growEkans = Tron.growEkans(this)

  def shrinkSnake = Tron.shrinkSnake(this)

  def shrinkEkans = Tron.shrinkEkans(this)
}

object Tron {
  def apply(start: Vertex): Tron =
    Tron(
      Snake(start, Nil, SnakeDirection.Up, SnakeStatus.Alive),
      Snake(start, Nil, SnakeDirection.Down, SnakeStatus.Alive)
    )

  def apply(x: Int, y: Int): Tron =
    Tron(
      Snake(Vertex(x.toDouble, y.toDouble), Nil, SnakeDirection.Up, SnakeStatus.Alive),
      Snake(Vertex(x.toDouble, y.toDouble), Nil, SnakeDirection.Down, SnakeStatus.Alive)
    )

  def update(
      tron: Tron,
      gridSize: BoundingBox,
      snakeCollisionCheck: Vertex => CollisionCheckOutcome,
      ekansCollisionCheck: Vertex => CollisionCheckOutcome
  ): (Tron, CollisionCheckResult) = {
    val s = tron.snake.update(gridSize, snakeCollisionCheck)
    val e = tron.ekans.update(gridSize, ekansCollisionCheck)
    (Tron(s._1, e._1), CollisionCheckResult.fromCollisionCheckOutcomes(s._2, e._2))
  }

  def growSnake(tron: Tron) = tron.copy(snake = tron.snake.grow)

  def growEkans(tron: Tron) = tron.copy(ekans = tron.ekans.grow)

  def shrinkSnake(tron: Tron) = tron.copy(snake = tron.snake.shrink)

  def shrinkEkans(tron: Tron) = tron.copy(ekans = tron.ekans.shrink)
}
