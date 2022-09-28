## Scores

Score is part of `GameModel` class and represented with an Int.

We could make it part of snake instead like we did for controlScheme but it makes less sense in that case.

For that one we could try to model it differently and keep it part of `GameModel`.

In fact scores appear in two places in the code, during the game and when game over.

For the game over part whatever model we chose it will be simple to show two different scores.

And during the game the score appears above the apple when picked and on the bottom right corner of the screen and in both cases this modelling should allow us to duplicate it easily.

Start by creating a `Score` model in `tronmodel` that defined both snake and ekans ones.

```scala
package lunatron.model.tronmodel

case class Scores(
    snake: Int,
    ekans: Int
)
```

Use it in `GameModel`

```scala
final case class GameModel(
  tron: Tron,
  gameState: GameState,
  gameMap: GameMap,
  scores: Scores,
  tickDelay: Seconds,
  lastUpdated: Seconds
)
```

Now update usages

in `initialModel` initialise both of them to 0

```scala
def initialModel(gridSize: BoundingBox): GameModel =
	GameModel(
	  tron = Tron(
	    gridSize.center.x.toInt,
	    gridSize.center.y.toInt - (gridSize.center.y / 2).toInt
	  ),
	  gameState = GameState.Running.start,
	  gameMap = GameMap.genLevel(gridSize),
	  scores = Scores(0, 0),
	  tickDelay = Seconds(0.1),
	  lastUpdated = Seconds.zero
	)
```

in `updateBasedOnCollision` in `PickUp` cases

```scala
scores = gameModel.scores.copy(snake = gameModel.scores.snake + ScoreIncrement)

/*** and ***/

scores = gameModel.scores.copy(ekans = gameModel.scores.ekans + ScoreIncrement)
```

Final scores appears in `GameOverScene`

As mentioned above we just want here to show both scores.

Let’s start with modifying the type of `SceneModel`

```scala
type SceneModel = Scores
```

Update `modelLens`

```scala
val modelLens: Lens[GameModel, SceneModel] =
    Lens.readOnly(_.scores)
```

Then `updateModel`

```scala
def updateModel(context: FrameContext[StartupData], pointsScored: Scores): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(pointsScored).addGlobalEvents(SceneEvent.JumpTo(StartScene.name))

    case _ =>
      Outcome(pointsScored)
  }
```

And `updateViewModel`

```scala
def updateViewModel(
      context: FrameContext[StartupData],
      pointsScored: Scores,
      sceneViewModel: Unit
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome(sceneViewModel)
```

And just the signature of `present`

```scala
def present(
      context: FrameContext[StartupData],
      pointsScored: Scores,
      sceneViewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
```

Now in the content of `present` we want to show both scores

```scala
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
```

Scores appears at the corner during the game which corresponds to `GameView`

Let’s start with using snake points as a starter value in `gameLayer` and compile it.

```scala
drawScore(viewConfig, currentState.scores.snake)
```

What we want to do here is to show on right corner Snake’s points in Red and on the left corner Ekans’ points in Blue.

I let you implement that part on your own !
