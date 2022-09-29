package lunatron.scenes

import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.events.{EventFilters, GlobalEvent, KeyboardEvent}
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}
import indigo.shared.constants.Key
import indigo.shared.datatypes.BindingKey
import indigo.shared.scenegraph.{Layer, SceneNode, SceneUpdateFragment, Text}
import lunatron.init.{GameAssets, StartupData}
import lunatron.model.{ControlScheme, GameModel, ViewModel}
import indigo.Seconds
import indigo.shared.collections.Batch

object ControlsScene extends Scene[StartupData, GameModel, ViewModel] {
  type SceneModel = Unit
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("controls")

  val modelLens: Lens[GameModel, SceneModel] =
    Lens.unit

  val viewModelLens: Lens[ViewModel, SceneViewModel] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted.withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(
      context: FrameContext[StartupData],
      sceneModel: SceneModel
  ): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(sceneModel).addGlobalEvents(SceneEvent.JumpTo(GameScene.name))
    case _ =>
      Outcome(sceneModel)
  }

  def updateViewModel(
      context: FrameContext[StartupData],
      sceneModel: SceneModel,
      sceneViewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome(sceneViewModel)

  def present(
      context: FrameContext[StartupData],
      sceneModel: SceneModel,
      sceneViewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = context.startUpData.viewConfig.horizontalCenter
      val verticalMiddle: Int = context.startUpData.viewConfig.verticalMiddle

      SceneUpdateFragment.empty.addLayer(
        Layer(
          BindingKey("ui"),
          Batch.fromList(
            drawControlsText(24, verticalMiddle) ++
              SharedElements.drawHitSpaceToStart(horizontalCenter, Seconds(1), context.gameTime)
          )
        )
      )
    }

  def drawControlsText(center: Int, middle: Int): List[SceneNode] =
    List(
      Text("Tron controls:", center, middle - 60, 1, GameAssets.fontKey, GameAssets.fontMaterial).alignLeft,
      Text(
        "Snake, in Red",
        center,
        middle - 30,
        1,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignLeft,
      Text(
        "is controlled with all arrow keys",
        center,
        middle - 20,
        1,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignLeft,
      Text(
        "Ekans, in Blue",
        center,
        middle + 10,
        1,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignLeft,
      Text(
        "is ontrolled with WASD keys",
        center,
        middle + 20,
        1,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignLeft
    )
}
