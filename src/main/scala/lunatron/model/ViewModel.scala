package lunatron.model

import indigo.shared.collections.Batch
import indigo.shared.scenegraph.Group
import lunatron.init.StartupData
import lunatron.scenes.GameView
import lunatron.model.GameModel

final case class ViewModel(walls: Group)
object ViewModel {
  def initialViewModel(startupData: StartupData, model: GameModel): ViewModel =
    ViewModel(
      walls = Group(Batch.fromList(
        model.gameMap.findWalls.map { wall =>
          startupData.staticAssets.wall
                     .moveTo(
                       GameView.gridPointToPoint(wall.gridPoint, startupData.viewConfig.gridSize, startupData.viewConfig.gridSquareSize)
                     )
        })
      )
    )
}
