package lunatron.scenes

import indigo.scenes.{Lens, Scene, SceneName}
import indigo.shared.events.{EventFilters, GlobalEvent}
import indigo.shared.scenegraph.{Group, SceneUpdateFragment}
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}
import lunatron.init.{GameAssets, StartupData}
import lunatron.model.{GameModel, ViewModel}
import lunatron.Score

object GameScene extends Scene[StartupData, GameModel, ViewModel] {
  type SceneModel     = GameModel
  type SceneViewModel = Group

  val name: SceneName =
    SceneName("game scene")

  val modelLens: Lens[GameModel, GameModel] =
    Lens.keepLatest

  val viewModelLens: Lens[ViewModel, Group] =
    Lens.readOnly(_.walls)

  val eventFilters: EventFilters =
    EventFilters.Restricted
                .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set(Score.automataSubSystem(GameModel.ScoreIncrement.toString(), GameAssets.fontKey))

  def updateModel(context: FrameContext[StartupData], gameModel: GameModel): GlobalEvent => Outcome[GameModel] =
    gameModel.update(context.gameTime, context.dice, context.startUpData.viewConfig.gridSquareSize)

  def updateViewModel(
    context: FrameContext[StartupData],
    gameModel: GameModel,
    walls: Group
  ): GlobalEvent => Outcome[Group] =
    _ => Outcome(walls)

  def present(
    context: FrameContext[StartupData],
    gameModel: GameModel,
    walls: Group
  ): Outcome[SceneUpdateFragment] =
    GameView.update(context.startUpData.viewConfig, gameModel, walls, context.startUpData.staticAssets)
}
