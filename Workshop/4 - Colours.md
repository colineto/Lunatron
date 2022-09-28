## Some colours

Go back to `GameView` in the `gameLayer` function

Here we previously added

```scala
drawEkans(viewConfig, currentState, staticAssets.snake) ++
```

but we used the same asset than for snake.

Let’s follow that `staticAsset` track and try to add a new one

`StaticAssets` are defined in `StartupData`

```scala
final case class StaticAssets(
  apple: Graphic[Material.Bitmap], 
  snake: Graphic[Material.Bitmap], 
  wall: Graphic[Material.Bitmap]
)
```

We can add ekans here, but I don’t know if you noticed that the whole game is in black and white and that’s also right for all the existing assets. Which means that if we want colour we will need to create our own assets or at least modify these ones.

Let’s trick a bit here. A `Bitmap` is in fact just an asset but instead we could use an `ImageEffect` which is a Bitmap but with more functionalities.

Update all the assets from `Bitmap` to `ImageEffect` and add Ekans

```scala
final case class StaticAssets(
    apple: Graphic[Material.ImageEffects],
    snake: Graphic[Material.ImageEffects],
    ekans: Graphic[Material.ImageEffects],
    wall: Graphic[Material.ImageEffects]
)
```

Now update `createStartupData` function

```scala
def createStartupData(viewConfig: ViewConfig): StartupData = {
    val blockSize = viewConfig.gridSquareSize

    StartupData(
      viewConfig = viewConfig,
      staticAssets = StaticAssets(
        apple = GameAssets.apple(blockSize),
        snake = GameAssets.snake(blockSize),
        ekans = GameAssets.ekans(blockSize),
        wall = GameAssets.wall(blockSize)
      )
    )
  }
```

Go into `GameAssets`

Update `snakeMaterial` and existing functions to `ImageEffects`

```scala
val snakeMaterial: Material.ImageEffects = Material.ImageEffects(snakeTexture)

def apple(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial).withCrop(blockSize, 0, blockSize, blockSize)

  def snake(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial)

  def wall(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial).withCrop(blockSize * 2, 0, blockSize, blockSize)
```

Use ImageEffects `withTint` function on the `snakeMaterial`, which will add a colour to a basic png asset, in the `snake` function

```scala
def snake(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial.withTint(RGBA.Blue))
```

Now you can add ekans one

```scala
  def ekans(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial.withTint(RGBA.Red))
```

Finally update **drawEkans** in `GameView`

```scala
drawEkans(viewConfig, currentState, staticAssets.ekans) ++
```

Let’s try it !
