## Some colours

Go to `GameView` in the `gameLayer` function

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

We can add ekans here 

```scala
final case class StaticAssets(
    apple: Graphic[Material.Bitmap],
    snake: Graphic[Material.Bitmap],
    ekans: Graphic[Material.Bitmap],
    wall: Graphic[Material.Bitmap]
)
```

I don’t know if you noticed but the whole game is in black and white and that’s also right for all the existing assets. Which means that if we want colour we will need to create our own assets or at least modify these ones.

Let’s trick a bit here. 

A `Bitmap` is a simple asset and there's not a lot we can do with it.

But instead we could use an `ImageEffect` which is an implementation of `Bitmap` but with more functionalities.

Update all the assets from `Bitmap` to `ImageEffect`.

```scala
final case class StaticAssets(
    apple: Graphic[Material.ImageEffects],
    snake: Graphic[Material.ImageEffects],
    ekans: Graphic[Material.ImageEffects],
    wall: Graphic[Material.ImageEffects]
)
```

Now update `createStartupData` function with ekans asset

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

As you can see we need a specific asset for ekans, so go into `GameAssets`

First, update `snakeMaterial` and existing functions with `ImageEffects` instead of `Bitmap`

```scala
val snakeMaterial: Material.ImageEffects = Material.ImageEffects(snakeTexture)

def apple(blockSize: Int): Graphic[Material.ImageEffects] =
  Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial).withCrop(blockSize, 0, blockSize, blockSize)

def snake(blockSize: Int): Graphic[Material.ImageEffects] =
  Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial)

def wall(blockSize: Int): Graphic[Material.ImageEffects] =
  Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial).withCrop(blockSize * 2, 0, blockSize, blockSize)
```

And here is the trick, in `ImageEffects` there's a `withTint` function which will add a colour to a basic png asset.

Apply that to the `snakeMaterial` of Snake with colour Blue.

```scala
def snake(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial.withTint(RGBA.Blue))
```

And create ekans function just like snake but with colour Red.

```scala
  def ekans(blockSize: Int): Graphic[Material.ImageEffects] =
    Graphic(0, 0, blockSize, blockSize, 2, GameAssets.snakeMaterial.withTint(RGBA.Red))
```

Finally update `drawEkans` call in `GameView`

```scala
drawEkans(viewConfig, currentState, staticAssets.ekans) ++
```

Let’s try it !
