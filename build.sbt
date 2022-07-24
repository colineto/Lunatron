lazy val lunatron =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .settings( // Normal SBT settings
      name         := "lunatronGame",
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
      windowStartWidth      := 720,
      windowStartHeight     := 516,
      disableFrameRateLimit := false,
      electronInstall       := indigoplugin.ElectronInstall.Global,
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "indigo-json-circe" % "0.13.0",
        "io.indigoengine" %%% "indigo"            % "0.13.0",
        "io.indigoengine" %%% "indigo-extras"     % "0.13.0"
      )
    )

scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8",                         // Specify character encoding used by source files.
      "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds",         // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings",              // Fail the compilation if there are any warnings.
      "-language:strictEquality"       // Scala 3 - Multiversal Equality
    )

addCommandAlias("buildGame", ";compile;fastOptJS;indigoBuild")
addCommandAlias("runGame", ";compile;fastOptJS;indigoRun")
