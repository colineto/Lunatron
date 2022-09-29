package lunatron.scenes

import indigo.shared.scenegraph.{Graphic, Group, Layer, SceneNode, SceneUpdateFragment, Text}
import indigo.shared.Outcome
import indigo.shared.datatypes.{BindingKey, Point}
import indigoextras.geometry.{BoundingBox, Vertex}
import lunatron.init.{GameAssets, StaticAssets, ViewConfig}
import lunatron.model.{GameMap, GameModel}

object GameView {

  def update(
      viewConfig: ViewConfig,
      model: GameModel,
      walls: Group,
      staticAssets: StaticAssets
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty.addLayer(
        Layer(
          BindingKey("game"),
          gameLayer(
            viewConfig,
            model,
            staticAssets,
            walls
          ): _*
        )
      )
    )

  def gameLayer(
      viewConfig: ViewConfig,
      currentState: GameModel,
      staticAssets: StaticAssets,
      walls: Group
  ): List[SceneNode] =
    walls ::
      drawApple(viewConfig, currentState.gameMap, staticAssets) ++
      drawSnake(viewConfig, currentState, staticAssets.snake) ++
      drawEkans(viewConfig, currentState, staticAssets.snake) ++
      drawScore(viewConfig, currentState.score)

  def drawApple(viewConfig: ViewConfig, gameMap: GameMap, staticAssets: StaticAssets): List[Graphic[_]] =
    gameMap.findApples.map { a =>
      staticAssets.apple.moveTo(gridPointToPoint(a.gridPoint, gameMap.gridSize, viewConfig.gridSquareSize))
    }

  def drawSnake(viewConfig: ViewConfig, currentState: GameModel, snakeAsset: Graphic[_]): List[Graphic[_]] =
    currentState.tron.snake.givePath.map { pt =>
      snakeAsset.moveTo(gridPointToPoint(pt, currentState.gameMap.gridSize, viewConfig.gridSquareSize))
    }

  def drawEkans(viewConfig: ViewConfig, currentState: GameModel, ekansAsset: Graphic[_]): List[Graphic[_]] =
    currentState.tron.ekans.givePath.map { pt =>
      ekansAsset.moveTo(gridPointToPoint(pt, currentState.gameMap.gridSize, viewConfig.gridSquareSize))
    }

  def drawScore(viewConfig: ViewConfig, score: Int): List[SceneNode] =
    List(
      Text(
        score.toString,
        (viewConfig.viewport.width / viewConfig.magnificationLevel) - 3,
        (viewConfig.viewport.height / viewConfig.magnificationLevel) - viewConfig.footerHeight + 21,
        1,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignRight
    )

  def gridPointToPoint(gridPoint: Vertex, gridSize: BoundingBox, gridSquareSize: Int): Point =
    Point((gridPoint.x * gridSquareSize).toInt, (((gridSize.height - 1) - gridPoint.y) * gridSquareSize).toInt)

}
