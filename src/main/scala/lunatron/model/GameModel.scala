package lunatron.model

import indigo.shared.dice.Dice
import indigo.shared.events.{FrameTick, GlobalEvent, KeyboardEvent, PlaySound}
import indigo.shared.time.{GameTime, Seconds}
import indigo.shared.Outcome
import indigo.shared.audio.Volume
import indigoextras.geometry.{BoundingBox, Vertex}
import lunatron.init.GameAssets
import lunatron.model.snakemodel.{CollisionCheckOutcome, Snake}
import lunatron.scenes.{GameOverScene, GameView}
import lunatron.Score
import indigo.scenes.SceneEvent
import lunatron.model.tronmodel.{CollisionCheckResult, Tron}

final case class GameModel(
    tron: Tron,
    gameState: GameState,
    gameMap: GameMap,
    score: Int,
    tickDelay: Seconds,
    controlScheme: ControlScheme,
    lastUpdated: Seconds
) {

  def update(gameTime: GameTime, dice: Dice, gridSquareSize: Int): GlobalEvent => Outcome[GameModel] = {
    case FrameTick if gameTime.running < lastUpdated + tickDelay =>
      Outcome(this)

    case FrameTick =>
      gameState match {
        case s @ GameState.Running(_, _, _) =>
          GameModel.updateRunning(
            gameTime,
            dice,
            this.copy(lastUpdated = gameTime.running),
            s,
            gridSquareSize
          )(FrameTick)

        case s @ GameState.Crashed(_, _, _, _, _) =>
          GameModel.updateCrashed(
            gameTime,
            this.copy(lastUpdated = gameTime.running),
            s
          )(FrameTick)
      }

    case e =>
      gameState match {
        case s @ GameState.Running(_, _, _) =>
          GameModel.updateRunning(gameTime, dice, this, s, gridSquareSize)(e)

        case s @ GameState.Crashed(_, _, _, _, _) =>
          GameModel.updateCrashed(gameTime, this, s)(e)
      }
  }

}

object GameModel {

  val ScoreIncrement: Int = 100

  def initialModel(gridSize: BoundingBox, controlScheme: ControlScheme): GameModel =
    GameModel(
      tron = Tron(
        gridSize.center.x.toInt,
        gridSize.center.y.toInt - (gridSize.center.y / 2).toInt
      ),
      gameState = GameState.Running.start,
      gameMap = GameMap.genLevel(gridSize),
      score = 0,
      tickDelay = Seconds(0.1),
      controlScheme = controlScheme,
      lastUpdated = Seconds.zero
    )

  def updateRunning(
      gameTime: GameTime,
      dice: Dice,
      state: GameModel,
      runningDetails: GameState.Running,
      gridSquareSize: Int
  ): GlobalEvent => Outcome[GameModel] = {
    case FrameTick =>
      val (updatedModel, collisionResult) =
        state.tron.update(
          state.gameMap.gridSize,
          hitTest(state.gameMap, state.tron.snake.givePath),
          hitTest(state.gameMap, state.tron.ekans.givePath)
        ) match {
          case (t, outcome) =>
            (
              state.copy(
                tron = t,
                gameState = state.gameState.updateNow(
                  gameTime.running,
                  state.tron.snake.direction,
                  state.tron.ekans.direction
                )
              ),
              outcome
            )
        }

      updateBasedOnCollision(gameTime, dice, gridSquareSize, updatedModel, collisionResult)

    case e: KeyboardEvent =>
      Outcome(
        state.copy(
          tron = Tron(
            state.controlScheme.instructSnake(e, state.tron.snake, runningDetails.lastSnakeDirection),
            state.controlScheme.instructSnake(e, state.tron.ekans, runningDetails.lastEkansDirection)
          )
        )
      )

    case _ =>
      Outcome(state)
  }

  def hitTest(gameMap: GameMap, body: List[Vertex]): Vertex => CollisionCheckOutcome =
    given CanEqual[Option[MapElement], Option[MapElement]] = CanEqual.derived
    pt =>
      if (body.contains(pt)) CollisionCheckOutcome.Crashed(pt)
      else
        gameMap.fetchElementAt(pt) match {
          case Some(MapElement.Apple(_)) =>
            CollisionCheckOutcome.PickUp(pt)

          case Some(MapElement.Wall(_)) =>
            CollisionCheckOutcome.Crashed(pt)

          case None =>
            CollisionCheckOutcome.NoCollision(pt)
        }

  def updateBasedOnCollision(
      gameTime: GameTime,
      dice: Dice,
      gridSquareSize: Int,
      gameModel: GameModel,
      collisionResult: CollisionCheckResult
  ): Outcome[GameModel] =
    collisionResult match {
      case CollisionCheckResult.SnakeCrashed(_) =>
        Outcome(
          gameModel.copy(
            gameState = gameModel.gameState match {
              case c @ GameState.Crashed(_, _, _, _, _) =>
                c

              case r @ GameState.Running(_, _, _) =>
                r.crash(gameTime.running, gameModel.tron.snake.length)
            },
            tickDelay = gameModel.tron.snake.length match {
              case l if l < 5  => Seconds(0.1)
              case l if l < 10 => Seconds(0.05)
              case l if l < 25 => Seconds(0.025)
              case _           => Seconds(0.015)
            }
          )
        ).addGlobalEvents(PlaySound(GameAssets.soundLose, Volume.Max))

      case CollisionCheckResult.EkansCrashed(_) =>
        Outcome(
          gameModel.copy(
            gameState = gameModel.gameState match {
              case c @ GameState.Crashed(_, _, _, _, _) =>
                c

              case r @ GameState.Running(_, _, _) =>
                r.crash(gameTime.running, gameModel.tron.ekans.length)
            },
            tickDelay = gameModel.tron.ekans.length match {
              case l if l < 5  => Seconds(0.1)
              case l if l < 10 => Seconds(0.05)
              case l if l < 25 => Seconds(0.025)
              case _           => Seconds(0.015)
            }
          )
        ).addGlobalEvents(PlaySound(GameAssets.soundLose, Volume.Max))

      case CollisionCheckResult.SnakePickUp(collision) =>
        Outcome(
          gameModel.copy(
            tron = gameModel.tron.growSnake,
            gameMap = gameModel.gameMap
              .removeApple(collision.gridPoint)
              .insertApple(
                MapElement.Apple(
                  gameModel.gameMap.findEmptySpace(dice, collision.gridPoint :: gameModel.tron.snake.givePath)
                )
              ),
            score = gameModel.score + ScoreIncrement
          )
        ).addGlobalEvents(
          PlaySound(GameAssets.soundPoint, Volume.Max),
          Score.spawnEvent(GameView.gridPointToPoint(collision.gridPoint, gameModel.gameMap.gridSize, gridSquareSize))
        )

      case CollisionCheckResult.EkansPickUp(collision) =>
        Outcome(
          gameModel.copy(
            tron = gameModel.tron.growEkans,
            gameMap = gameModel.gameMap
              .removeApple(collision.gridPoint)
              .insertApple(
                MapElement.Apple(
                  gameModel.gameMap.findEmptySpace(dice, collision.gridPoint :: gameModel.tron.ekans.givePath)
                )
              ),
            score = gameModel.score + ScoreIncrement
          )
        ).addGlobalEvents(
          PlaySound(GameAssets.soundPoint, Volume.Max),
          Score.spawnEvent(GameView.gridPointToPoint(collision.gridPoint, gameModel.gameMap.gridSize, gridSquareSize))
        )

      case CollisionCheckResult.BothNoCollision(_, _) =>
        Outcome(gameModel)
    }

  def updateCrashed(
      gameTime: GameTime,
      state: GameModel,
      crashDetails: GameState.Crashed
  ): GlobalEvent => Outcome[GameModel] = {
    case FrameTick if gameTime.running <= crashDetails.crashedAt + Seconds(0.75) =>
      // Pause briefly on collision
      Outcome(state)

    case FrameTick if state.tron.snake.length > 1 =>
      Outcome(
        state.copy(
          tron = state.tron.shrinkSnake,
          gameState = state.gameState
            .updateNow(gameTime.running, state.gameState.lastSnakeDirection, state.gameState.lastEkansDirection)
        )
      )

    case FrameTick if state.tron.ekans.length > 1 =>
      Outcome(
        state.copy(
          tron = state.tron.shrinkEkans,
          gameState = state.gameState
            .updateNow(gameTime.running, state.gameState.lastSnakeDirection, state.gameState.lastEkansDirection)
        )
      )

    case FrameTick if state.tron.snake.length == 1 =>
      Outcome(state).addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))

    case FrameTick if state.tron.ekans.length == 1 =>
      Outcome(state).addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))

    case _ =>
      Outcome(state)
  }

}
