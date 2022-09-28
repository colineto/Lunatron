## Collision rule

Go in `GameModel`

We already have all the cases handled in `updateBasedOnCollision`

We need to know when a snake just hit the body of the other one

This will be handled in `hitTest`

In `updateRunning` we have two `hitTests`, one for snake and one for ekans

In both of them we will need to add information about the body of the other snake so we can also raise a `Crashed` state when they hit each other.

Add **otherBody** to the signature

```scala
def hitTest(gameMap: GameMap, body: List[Vertex], otherBody: List[Vertex]): Vertex => CollisionCheckOutcome =
```

The if check that it hit himself

```scala
if (body.contains(pt)) CollisionCheckOutcome.Crashed(pt)
```

We can do the same to test it hit the otherBody

```scala
else if (otherBody.contains(pt)) CollisionCheckOutcome.Crashed(pt)
```

Now you can add the other bodies in `updateRunning`

```scala
hitTest(state.gameMap, state.tron.snake.givePath, state.tron.ekans.givePath),
hitTest(state.gameMap, state.tron.ekans.givePath, state.tron.snake.givePath)
```

Try it !
