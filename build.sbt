import com.typesafe.sbt.SbtPgp.autoImportImpl.pgpSecretRing
import sbt.file

name := "cats-saga"

val mainScala = "2.13.6"
val allScala  = Seq("2.11.12", mainScala, "2.12.14")

inThisBuild(
  List(
    organization := "com.vladkopanev",
    homepage := Some(url("https://github.com/VladKopanev/cats-saga")),
    licenses := List("MIT License" -> url("https://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        "VladKopanev",
        "Vladislav Kopanev",
        "ivengo53@gmail.com",
        url("http://vladkopanev.com")
      )
    ),
    scmInfo := Some(
      ScmInfo(url("https://github.com/VladKopanev/cats-saga"), "scm:git:git@github.com/VladKopanev/cats-saga.git")
    ),
    pgpPublicRing := file("./travis/local.pubring.asc"),
    pgpSecretRing := file("./travis/local.secring.asc"),
    releaseEarlyWith := SonatypePublisher
  )
)

lazy val commonSettings = Seq(
  scalaVersion := mainScala,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-explaintypes",
    "-Yrangepos",
    "-feature",
    "-Xfuture",
    "-language:higherKinds",
    "-language:existentials",
    "-language:implicitConversions",
    "-unchecked",
    "-Xlint:_,-type-parameter-shadow",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-value-discard"
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) =>
      Seq(
        "-Yno-adapted-args",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit"
      )
    case Some((2, 12)) =>
      Seq(
        "-Xsource:2.13",
        "-Yno-adapted-args",
        "-Ypartial-unification",
        "-Ywarn-extra-implicit",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit",
        "-opt-inline-from:<source>",
        "-opt-warnings",
        "-opt:l:inline"
      )
    case _ => Nil
  }),
  resolvers ++= Seq(Resolver.sonatypeRepo("snapshots"), Resolver.sonatypeRepo("releases"))
)

lazy val root = project
  .in(file("."))
  .aggregate(core)

val catsVersion = "3.1.0"
val catsRetryVersion = "3.0.0"
val scalaTestVersion = "3.2.9"
val kindProjectorVersion = "0.13.0"
val disciplineCoreVersion = "1.1.3"
val disciplineScalatestVersion = "2.1.1"

lazy val core = project
  .in(file("core"))
  .settings(
    commonSettings,
    name := "cats-saga",
    crossScalaVersions := allScala,
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-effect"               % catsVersion,
      "org.typelevel"              %% "cats-laws"                 % "2.6.1"       % Test,
      "org.typelevel"              %% "cats-effect-laws"          % catsVersion       % Test,
      "org.typelevel"              %% "cats-effect-testkit"       % catsVersion       % Test,
      "org.scalatest"              %% "scalatest"                 % scalaTestVersion  % Test,
      "org.typelevel"              %% "discipline-core"           % disciplineCoreVersion % Test,
      "org.typelevel"              %% "discipline-scalatest"      % disciplineScalatestVersion % Test,
      "com.github.cb372"           %% "cats-retry"                % catsRetryVersion  % Optional,
      compilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full)
    )
  )

val http4sVersion   = "0.23.0-RC1"
val log4CatsVersion = "1.1.1"
val doobieVersion   = "1.0.0-M5"
val circeVersion    = "0.14.1"

lazy val examples = project
  .in(file("examples"))
  .settings(
    commonSettings,
    coverageEnabled := false,
    libraryDependencies ++= Seq(
      "ch.qos.logback"    % "logback-classic"          % "1.2.3",
      "com.github.cb372"  %% "cats-retry"              % catsRetryVersion,
      "io.chrisdavenport" %% "log4cats-core"           % log4CatsVersion,
      "io.chrisdavenport" %% "log4cats-noop"           % log4CatsVersion,
      "io.circe"          %% "circe-generic"           % circeVersion,
      "io.circe"          %% "circe-parser"            % circeVersion,
      "org.http4s"        %% "http4s-circe"            % http4sVersion,
      "org.http4s"        %% "http4s-dsl"              % http4sVersion,
      "org.http4s"        %% "http4s-blaze-server"     % http4sVersion,
      "org.tpolecat"      %% "doobie-core"             % doobieVersion,
      "org.tpolecat"      %% "doobie-hikari"           % doobieVersion,
      "org.tpolecat"      %% "doobie-postgres"         % doobieVersion,
      compilerPlugin("org.typelevel"    %% "kind-projector"     % kindProjectorVersion cross CrossVersion.full),
      compilerPlugin("com.olegpy"       %% "better-monadic-for" % "0.3.1")
    )
  )
  .dependsOn(core % "compile->compile")
