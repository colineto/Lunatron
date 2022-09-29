package lunatron.init

import indigo.shared.{Outcome, Startup}
import indigo.shared.config.GameViewport
import indigo.shared.materials.Material
import indigo.shared.scenegraph.Graphic
import indigoextras.geometry.BoundingBox

object StartupData {

  def initialise(
      viewConfig: ViewConfig
  ): Outcome[Startup[StartupData]] =
    Outcome(
      Startup.Success(createStartupData(viewConfig))
    )

  def createStartupData(viewConfig: ViewConfig): StartupData = {
    val blockSize = viewConfig.gridSquareSize

    StartupData(
      viewConfig = viewConfig,
      staticAssets = StaticAssets(
        apple = GameAssets.apple(blockSize),
        snake = GameAssets.snake(blockSize),
        ekans = GameAssets.ekans(blockSize),
        wall = GameAssets.wall(blockSize)
      )
    )
  }

}

final case class StartupData(viewConfig: ViewConfig, staticAssets: StaticAssets)

final case class StaticAssets(
    apple: Graphic[Material.ImageEffects],
    snake: Graphic[Material.ImageEffects],
    ekans: Graphic[Material.ImageEffects],
    wall: Graphic[Material.ImageEffects]
)

final case class ViewConfig(
    gridSize: BoundingBox,
    gridSquareSize: Int,
    footerHeight: Int,
    magnificationLevel: Int,
    viewport: GameViewport
) {
  val horizontalCenter: Int = (viewport.width / magnificationLevel) / 2
  val verticalMiddle: Int = (viewport.height / magnificationLevel) / 2
}
object ViewConfig {

  val default: ViewConfig = {
    val gridSquareSize = 12
    val gridSize = BoundingBox(
      x = 0,
      y = 0,
      width = 30,
      height = 20
    )
    val magnificationLevel = 2
    val footerHeight = 36

    ViewConfig(
      gridSize,
      gridSquareSize,
      footerHeight,
      magnificationLevel,
      GameViewport(
        (gridSquareSize * gridSize.width.toInt) * magnificationLevel,
        ((gridSquareSize * gridSize.height.toInt) * magnificationLevel) + footerHeight
      )
    )
  }

}
