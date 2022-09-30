## Collision rules

First let's remember what are the collision rules

The rules that applies to Snake game and that still apply to Tron game:

* if it crosses a wall it dies
* if it crosses itself it dies
* if it eats an apple it grows

The rule we need to add to make it a Tron:

* if it crosses the other snake it dies

Let's implement that last rule

Go in `GameModel`. In here we have 2 places where collision is used.

* `updateBasedOnCollision`
* `hitTest`

In `updateBasedOnCollision` we handle `SnakeCrashed` and `EkansCrashed` cases already, so it seems that's not the place to introduce another type of crash.

In `hitTest` we take a snake body and compare it to the actual position point. To see if that snake body just met an apple or a wall. This seems a much better place to test if that snake body did hurt another snake body :)

Add an 'otherBody' to `hitTest`

```scala
def hitTest(gameMap: GameMap, body: List[Vertex], otherBody: List[Vertex]): Vertex => CollisionCheckOutcome =
```

Then check if it hit the other body

```scala
???
```

Now add the 'otherBody' parameter in `updateRunning`, `hitTest` calls

```scala
hitTest(state.gameMap, state.tron.snake.givePath, state.tron.ekans.givePath),
hitTest(state.gameMap, state.tron.ekans.givePath, state.tron.snake.givePath)
```

If you too, you noticed that this could definitely be improved with, keep it in mind for later !

Now Try it :)
