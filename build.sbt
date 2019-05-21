name := "parallel-pi"

version := "0.1"

scalaVersion := "2.12.8"

// https://mvnrepository.com/artifact/commons-cli/commons-cli
libraryDependencies += "commons-cli" % "commons-cli" % "1.4"

// https://mvnrepository.com/artifact/org.apfloat/apfloat
libraryDependencies += "org.apfloat" % "apfloat" % "1.8.3"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
