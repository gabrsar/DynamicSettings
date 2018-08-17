name := "dynamicsettings"
organization := "br.com.gabrielsaraiva"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.388"
