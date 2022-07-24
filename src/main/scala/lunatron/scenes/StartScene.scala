package lunatron.scenes

import indigo.scenes.{Lens, Scene, SceneName}
import indigo.Seconds
import indigo.shared.events.{EventFilters, GlobalEvent, KeyboardEvent}
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}
import indigo.shared.constants.Key
import indigo.shared.datatypes.BindingKey
import indigo.shared.scenegraph.{Layer, PlaybackPattern, SceneAudio, SceneAudioSource, SceneNode, SceneUpdateFragment, Text}
import lunatron.init.{GameAssets, StartupData}
import lunatron.model.{GameModel, ViewModel}
import lunatron.scenes.ControlsScene
import lunatron.GameReset
import indigo.scenes.SceneEvent
import indigo.shared.audio.Track
import indigo.shared.collections.Batch

object StartScene extends Scene[StartupData, GameModel, ViewModel] {
  type SceneModel     = Unit
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("start")

  val modelLens: Lens[GameModel, Unit] =
    Lens.unit

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted
                .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(
    context: FrameContext[StartupData],
    snakeGameModel: Unit
  ): GlobalEvent => Outcome[Unit] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(snakeGameModel)
        .addGlobalEvents(
          GameReset,
          SceneEvent.JumpTo(ControlsScene.name)
        )

    case _ =>
      Outcome(snakeGameModel)
  }

  def updateViewModel(
    context: FrameContext[StartupData],
    snakeGameModel: Unit,
    snakeViewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(snakeViewModel)

  def present(
    context: FrameContext[StartupData],
    snakeGameModel: Unit,
    snakeViewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = context.startUpData.viewConfig.horizontalCenter
      val verticalMiddle: Int   = context.startUpData.viewConfig.verticalMiddle

      SceneUpdateFragment.empty
                         .addLayer(
                           Layer(
                             BindingKey("ui"),
                             Batch.fromList(drawTitleText(horizontalCenter, verticalMiddle) ++
                               SharedElements.drawHitSpaceToStart(horizontalCenter, Seconds(1), context.gameTime)
                             )
                           )
                         )
                         .withAudio(
                           SceneAudio(
                             SceneAudioSource(
                               BindingKey("intro music"),
                               PlaybackPattern.SingleTrackLoop(
                                 Track(GameAssets.soundIntro)
                               )
                             )
                           )
                         )
    }

  def drawTitleText(center: Int, middle: Int): List[SceneNode] =
    List(
      Text("snake!", center, middle - 20, 1, GameAssets.fontKey, GameAssets.fontMaterial).alignCenter,
      Text("presented in glorious 1 bit graphics", center, middle - 5, 1, GameAssets.fontKey, GameAssets.fontMaterial).alignCenter,
      Text("Made by Dave", center, middle + 10, 1, GameAssets.fontKey, GameAssets.fontMaterial).alignCenter
    )
}

