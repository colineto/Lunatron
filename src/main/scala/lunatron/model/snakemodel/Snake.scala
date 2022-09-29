package lunatron.model.snakemodel

import indigoextras.geometry.{BoundingBox, Vertex}
import indigo.shared.events.KeyboardEvent
import lunatron.model.ControlScheme

final case class Snake(
    start: Vertex,
    body: List[Vertex],
    direction: SnakeDirection,
    status: SnakeStatus,
    controlScheme: ControlScheme
) {

  def goUp: Snake =
    Snake.goUp(this)

  def goDown: Snake =
    Snake.goDown(this)

  def goLeft: Snake =
    Snake.goLeft(this)

  def goRight: Snake =
    Snake.goRight(this)

  def turnLeft: Snake =
    Snake.turnLeft(this)

  def turnRight: Snake =
    Snake.turnRight(this)

  def update(gridSize: BoundingBox, collisionCheck: Vertex => CollisionCheckOutcome): (Snake, CollisionCheckOutcome) =
    Snake.update(this, gridSize, collisionCheck)

  def end: Vertex =
    Snake.end(this)

  def grow: Snake =
    Snake.grow(this)

  def shrink: Snake =
    Snake.shrink(this)

  def crash: Snake =
    Snake.crash(this)

  def length: Int =
    1 + body.length

  def givePath: List[Vertex] =
    start :: body

  def givePathList: List[(Int, Int)] =
    (start :: body).map(p => (p.x.toInt, p.y.toInt))

  def instruct(keyboardEvent: KeyboardEvent): Snake =
    Snake.instruct(keyboardEvent, this)
}
object Snake {

  def apply(start: Vertex, controlScheme: ControlScheme): Snake =
    Snake(start, Nil, SnakeDirection.Up, SnakeStatus.Alive, controlScheme)

  def apply(x: Int, y: Int, controlScheme: ControlScheme): Snake =
    Snake(Vertex(x.toDouble, y.toDouble), Nil, SnakeDirection.Up, SnakeStatus.Alive, controlScheme)

  def turnLeft(snake: Snake): Snake =
    snake.copy(direction = snake.direction.turnLeft)

  def turnRight(snake: Snake): Snake =
    snake.copy(direction = snake.direction.turnRight)

  def goUp(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goUp)

  def goDown(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goDown)

  def goLeft(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goLeft)

  def goRight(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goRight)

  def end(snake: Snake): Vertex =
    snake.body.reverse.headOption.getOrElse(snake.start)

  def grow(snake: Snake): Snake =
    snake.copy(body = snake.body :+ end(snake))

  def shrink(snake: Snake): Snake =
    snake.copy(body = snake.body.dropRight(1))

  def crash(snake: Snake): Snake =
    snake.copy(status = SnakeStatus.Dead)

  def update(
      snake: Snake,
      gridSize: BoundingBox,
      collisionCheck: Vertex => CollisionCheckOutcome
  ): (Snake, CollisionCheckOutcome) =
    nextPosition(gridSize).andThen(collisionCheck).andThen(snakeUpdate(snake))(snake)

  def nextPosition(gridSize: BoundingBox): Snake => Vertex =
    snake =>
      wrap(
        snake.direction.oneSquareForward(snake.start),
        gridSize
      )

  def wrap(gridPoint: Vertex, gridSize: BoundingBox): Vertex =
    gridPoint.copy(
      x = if (gridPoint.x < 0) gridSize.width else gridPoint.x % gridSize.width,
      y = if (gridPoint.y < 0) gridSize.height else gridPoint.y % gridSize.height
    )

  def snakeUpdate(snake: Snake): CollisionCheckOutcome => (Snake, CollisionCheckOutcome) = {
    case oc @ CollisionCheckOutcome.NoCollision(pt) =>
      (moveToPosition(snake, pt), oc)

    case oc @ CollisionCheckOutcome.PickUp(pt) =>
      (moveToPosition(snake.grow, pt), oc)

    case oc @ CollisionCheckOutcome.Crashed(_) =>
      (snake.crash, oc)
  }

  def moveToPosition(snake: Snake, snakePoint: Vertex): Snake =
    snake match {
      case Snake(_, Nil, d, s, cs) =>
        Snake(snakePoint, Nil, d, s, cs)

      case Snake(h, l, d, s, cs) =>
        Snake(snakePoint, h :: l.reverse.drop(1).reverse, d, s, cs)
    }

  def instruct(keyboardEvent: KeyboardEvent, snake: Snake): Snake =
    (snake.controlScheme, keyboardEvent) match {
      case (ControlScheme.Turning(left, _), KeyboardEvent.KeyDown(code)) if code === left =>
        snake.direction.makeLegalTurn(snake.turnLeft).getOrElse(snake)

      case (ControlScheme.Turning(_, right), KeyboardEvent.KeyDown(code)) if code === right =>
        snake.direction.makeLegalTurn(snake.turnRight).getOrElse(snake)

      case (ControlScheme.Directed(up, _, _, _), KeyboardEvent.KeyDown(code)) if code === up =>
        snake.direction.makeLegalTurn(snake.goUp).getOrElse(snake)

      case (ControlScheme.Directed(_, down, _, _), KeyboardEvent.KeyDown(code)) if code === down =>
        snake.direction.makeLegalTurn(snake.goDown).getOrElse(snake)

      case (ControlScheme.Directed(_, _, left, _), KeyboardEvent.KeyDown(code)) if code === left =>
        snake.direction.makeLegalTurn(snake.goLeft).getOrElse(snake)

      case (ControlScheme.Directed(_, _, _, right), KeyboardEvent.KeyDown(code)) if code === right =>
        snake.direction.makeLegalTurn(snake.goRight).getOrElse(snake)

      case (ControlScheme.WASDTurning(left, _), KeyboardEvent.KeyDown(code)) if code === left =>
        snake.direction.makeLegalTurn(snake.turnLeft).getOrElse(snake)

      case (ControlScheme.WASDTurning(_, right), KeyboardEvent.KeyDown(code)) if code === right =>
        snake.direction.makeLegalTurn(snake.turnRight).getOrElse(snake)

      case (ControlScheme.WASDDirected(up, _, _, _), KeyboardEvent.KeyDown(code)) if code === up =>
        snake.direction.makeLegalTurn(snake.goUp).getOrElse(snake)

      case (ControlScheme.WASDDirected(_, down, _, _), KeyboardEvent.KeyDown(code)) if code === down =>
        snake.direction.makeLegalTurn(snake.goDown).getOrElse(snake)

      case (ControlScheme.WASDDirected(_, _, left, _), KeyboardEvent.KeyDown(code)) if code === left =>
        snake.direction.makeLegalTurn(snake.goLeft).getOrElse(snake)

      case (ControlScheme.WASDDirected(_, _, _, right), KeyboardEvent.KeyDown(code)) if code === right =>
        snake.direction.makeLegalTurn(snake.goRight).getOrElse(snake)

      case _ => snake
    }
}
