package lunatron

import indigo.shared.datatypes.{BindingKey, Point}
import indigo.shared.scenegraph.{SceneNode, Text}
import indigo.shared.datatypes.FontKey
import indigo.shared.temporal.{Signal, SignalReader}
import indigo.shared.time.Seconds
import indigoextras.subsystems.{Automata, AutomataEvent, AutomataPoolKey, Automaton, AutomatonNode, AutomatonSeedValues, AutomatonUpdate}
import lunatron.init.GameAssets

object Score {

  val poolKey: AutomataPoolKey =
    AutomataPoolKey("points")

  def automataSubSystem(scoreAmount: String, fontKey: FontKey): Automata =
    Automata(
      poolKey,
      Automaton(
        AutomatonNode.Fixed(Text(scoreAmount, 0, 0, 1, fontKey, GameAssets.fontMaterial).alignCenter),
        Seconds(1.5)
      ).withModifier(ModiferFunctions.signal),
      BindingKey("score")
    )

  val spawnEvent: Point => AutomataEvent =
    position => AutomataEvent.Spawn(poolKey, position, None, None)

  object ModiferFunctions {

    val workOutPosition: AutomatonSeedValues => Signal[Point] =
      seed =>
        Signal { time =>
          seed.spawnedAt +
            Point(
              0,
              -(30d * (seed.progression(time))).toInt
            )
        }

    val signal: SignalReader[(AutomatonSeedValues, SceneNode), AutomatonUpdate] =
      SignalReader {
        case (seed, sceneGraphNode) =>
          sceneGraphNode match {
            case t: Text[_] =>
              workOutPosition(seed).map { position =>
                AutomatonUpdate(t.moveTo(position))
              }

            case _ =>
              Signal.fixed(AutomatonUpdate.empty)
          }
      }

  }

}
