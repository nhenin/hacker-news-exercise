import sbt.Keys._


lazy val core = project.settings(CompilationDefaults.settings)

lazy val `client-protocol` = project
  .settings(CompilationDefaults.settings)
  .settings(
    libraryDependencies ++= LibraryBundles.akkaJson ++ (LibraryBundles.akkaScalatest % Test)
  )
  .dependsOn(core)

lazy val client = project
  .settings(CompilationDefaults.settings)
  .settings(
    libraryDependencies ++= LibraryBundles.akkaHttp
  ).dependsOn(core,`client-protocol`)

lazy val analytics = project
  .settings(CompilationDefaults.settings)
  .settings(
    libraryDependencies ++= LibraryBundles.akkaHttp ++ (LibraryBundles.akkaScalatest % Test)
  )
  .dependsOn(client)

lazy val `top-commenters-application` = project
  .settings(CompilationDefaults.settings)
  .settings(
    libraryDependencies ++= LibraryBundles.akkaHttp
  )
  .dependsOn(client,analytics)

lazy val backend = project
  .in(file("."))
  .aggregate( analytics , client, `client-protocol`, core)