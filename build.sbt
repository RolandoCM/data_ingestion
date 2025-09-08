ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

val sparkVersion = "3.3.0"
val kafkaVersion = "3.3.1"




lazy val root = (project in file("."))
  .settings(
    name := "news_ingestion",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      "org.apache.spark" %% "spark-streaming" % sparkVersion,
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,

      "org.apache.kafka" % "kafka-clients" % kafkaVersion,

      "com.typesafe" % "config" % "1.4.4",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.1",
      "org.playframework" %% "play-json" % "3.0.5",
      "org.scalaj" %% "scalaj-http" % "2.4.2",
      "org.jsoup" % "jsoup" % "1.15.3"
    )
  )
