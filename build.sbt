ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "ScOOlang"
  )

val jarName = "ScOOlang.jar"
assembly/assemblyJarName := jarName

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test"