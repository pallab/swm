ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / assemblyMergeStrategy := {
  case "module-info.class" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "swm"
  )

val slick = "com.typesafe.slick" %% "slick" % "3.3.3"
val playJson = "com.typesafe.play" %% "play-json" % "2.9.2"
val h2 = "com.h2database" % "h2" % "1.4.197"
val akka = "com.typesafe.akka" %% "akka-actor" % "2.6.13"
val guava = "com.google.guava" % "guava" % "28.1-jre"
val scalatest = "org.scalatest" %% "scalatest" % "3.2.7" % Test

libraryDependencies ++= Seq(akka, h2, slick, playJson, guava, scalatest)
