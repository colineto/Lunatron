package lunatron.scenes

import indigo.scenes.{Lens, Scene, SceneName}
import indigo.shared.events.{EventFilters, GlobalEvent, KeyboardEvent}
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}
import indigo.shared.constants.Key
import indigo.shared.datatypes.BindingKey
import indigo.shared.scenegraph.{Layer, SceneUpdateFragment, Text}
import lunatron.init.{GameAssets, StartupData}
import lunatron.model.{GameModel, ViewModel}
import indigo.scenes.SceneEvent
import lunatron.model.tronmodel.Scores

object GameOverScene extends Scene[StartupData, GameModel, ViewModel] {
  type SceneModel = Scores
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("game over")

  val modelLens: Lens[GameModel, SceneModel] =
    Lens.readOnly(_.scores)

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(context: FrameContext[StartupData], pointsScored: Scores): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(pointsScored).addGlobalEvents(SceneEvent.JumpTo(StartScene.name))

    case _ =>
      Outcome(pointsScored)
  }

  def updateViewModel(
      context: FrameContext[StartupData],
      pointsScored: Scores,
      sceneViewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome(sceneViewModel)

  def present(
      context: FrameContext[StartupData],
      pointsScored: Scores,
      sceneViewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = context.startUpData.viewConfig.horizontalCenter
      val verticalMiddle: Int = context.startUpData.viewConfig.verticalMiddle

      SceneUpdateFragment.empty.addLayer(
        Layer(
          BindingKey("ui"),
          Text(
            "Game Over!",
            horizontalCenter,
            verticalMiddle - 40,
            1,
            GameAssets.fontKey,
            GameAssets.fontMaterial
          ).alignCenter,
          Text(
            s"Snake scored: ${pointsScored.snake.toString()} pts!",
            horizontalCenter,
            verticalMiddle - 20,
            1,
            GameAssets.fontKey,
            GameAssets.fontMaterial
          ).alignCenter,
          Text(
            s"Ekans scored: ${pointsScored.ekans.toString()} pts!",
            horizontalCenter,
            verticalMiddle,
            1,
            GameAssets.fontKey,
            GameAssets.fontMaterial
          ).alignCenter,
          Text(
            "(hit space to restart)",
            horizontalCenter,
            220,
            1,
            GameAssets.fontKey,
            GameAssets.fontMaterial
          ).alignCenter
        )
      )
    }
}
