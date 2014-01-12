import AssemblyKeys._

name := "firefly-simulator"

version := "0.1.0"
 
scalaVersion := "2.10.3"

// SBT-Eclipse settings
EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE17)

EclipseKeys.withSource := true
  
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.0" % "test->default",
  "org.scalafx" %% "scalafx" % "1.0.0-M6",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "org.slf4j" % "slf4j-log4j12" % "1.7.5"
)

assemblySettings

jarName in assembly := "firefly-simulator"

mainClass in assembly := Some("ui.Main")

test in assembly := {}