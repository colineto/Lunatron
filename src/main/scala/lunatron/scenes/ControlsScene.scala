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
  type SceneModel = ControlScheme
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("controls")

  val modelLens: Lens[GameModel, ControlScheme] =
    Lens(_.controlScheme, (m, c) => m.copy(controlScheme = c))

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted
                .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(
    context: FrameContext[StartupData],
    controlScheme: ControlScheme
  ): GlobalEvent => Outcome[ControlScheme] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(controlScheme)
        .addGlobalEvents(SceneEvent.JumpTo(GameScene.name))

    case KeyboardEvent.KeyUp(Key.UP_ARROW) | KeyboardEvent.KeyUp(Key.DOWN_ARROW) =>
      Outcome(controlScheme.swap)

    case _ =>
      Outcome(controlScheme)
  }

  def updateViewModel(
    context: FrameContext[StartupData],
    controlScheme: ControlScheme,
    sceneViewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(sceneViewModel)

  def present(
    context: FrameContext[StartupData],
    sceneModel: ControlScheme,
    sceneViewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = context.startUpData.viewConfig.horizontalCenter
      val verticalMiddle: Int = context.startUpData.viewConfig.verticalMiddle

      SceneUpdateFragment.empty
                         .addLayer(
                           Layer(
                             BindingKey("ui"),
                             Batch.fromList(drawControlsText(24, verticalMiddle, sceneModel) ++
                               List(drawSelectText(horizontalCenter)) ++
                               SharedElements.drawHitSpaceToStart(horizontalCenter, Seconds(1), context.gameTime)
                             )
                           )
                         )
    }

  def drawControlsText(center: Int, middle: Int, controlScheme: ControlScheme): List[SceneNode] =
    List(
      Text("select controls", center, middle - 20, 1, GameAssets.fontKey, GameAssets.fontMaterial).alignLeft
    ) ++ {
      controlScheme match {
        case ControlScheme.Turning(_, _) =>
          List(
            Text(
              "[_] direction (all arrow keys)", center, middle - 5, 1, GameAssets.fontKey, GameAssets.fontMaterial
            ).alignLeft,
            Text(
              "[x] turn (left and right arrows)", center, middle + 10, 1, GameAssets.fontKey, GameAssets.fontMaterial
            ).alignLeft
          )

        case ControlScheme.Directed(_, _, _, _) =>
          List(
            Text(
              "[x] direction (all arrow keys)", center, middle - 5, 1, GameAssets.fontKey, GameAssets.fontMaterial
            ).alignLeft,
            Text(
              "[_] turn (left and right arrows)", center, middle + 10, 1, GameAssets.fontKey, GameAssets.fontMaterial
            ).alignLeft
          )
      }
    }

  def drawSelectText(center: Int): SceneNode =
    Text("Up / Down arrows to select.", center, 205, 1, GameAssets.fontKey, GameAssets.fontMaterial).alignCenter
}
