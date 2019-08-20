name := "wordcount"
version := "0.1"
organization := "com.monksy.kafka"
scalaVersion := "2.12.9"


// Always fork the jvm (test and run)
fork := true

// Allow CTRL-C to cancel running tasks without exiting SBT CLI.
cancelable in Global := true
lazy val kafkaVer = "2.3.0"

libraryDependencies ++= Seq(

  // scala wrapper for kafka streams DSL:
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaVer,

  // Tracing libs
  "io.opentracing.contrib" % "opentracing-kafka-streams" % "0.1.4",
  "io.jaegertracing" % "jaeger-client" % "1.0.0",

  // config
  "com.github.pureconfig" %% "pureconfig" % "0.9.1",

  // logging
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

)

// Print full stack traces in tests:
testOptions in Test += Tests.Argument("-oF")

// Assembly stuff (for fat jar)
mainClass in assembly := Some("com.monksy.kafka.wordcount.WordCount")
assemblyJarName in assembly := "kafka-streams-scala-wordcount.jar"

// Some stuff to import in the console
initialCommands in console :=
  """
  // project stuff
  import com.monksy.kafka.wordcount._
"""