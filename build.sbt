lazy val lunatron =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .settings( // Normal SBT settings
      name         := "lunatron",
      version      := "0.0.1",
      scalaVersion := "3.1.2",
      organization := "lunatech",
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework")
    )
    .settings( // Indigo specific settings
      showCursor            := true,
      title                 := "Tron game by Lunatech",
      gameAssetsDirectory   := "assets",
      windowStartWidth      := 550,
      windowStartHeight     := 400,
      disableFrameRateLimit := false,
      electronInstall       := indigoplugin.ElectronInstall.Global,
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "indigo-json-circe" % "0.13.0",
        "io.indigoengine" %%% "indigo"            % "0.13.0",
        "io.indigoengine" %%% "indigo-extras"     % "0.13.0"
      )
    )

addCommandAlias("buildGame", ";compile;fastOptJS;indigoBuild")
addCommandAlias("runGame", ";compile;fastOptJS;indigoRun")
