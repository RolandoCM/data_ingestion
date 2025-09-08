package com.itquetzali.news.util

import com.typesafe.config.{ Config, ConfigFactory}

object ConfigLoader {
  private val config: Config = ConfigFactory.load()

  val kafkaBootstrapServers: String = config.getString("kafka.bootstrap.servers")
  val kafkaTopic: String = config.getString("kafka.topic")

  val newsApiKey: String = config.getString("newsapi.api-key")
  val newsApiBaseUrl: String = config.getString("newsapi.base-url")
  val newsSources: String = config.getString("newsapi.sources")

  val sparkAppName: String = config.getString("spark.app-name")
  val sparkMaster: String = config.getString("spark.master")

  val batchInterval: Int = config.getInt("etl.batch-interval")
  val maxArticles: Int = config.getInt("etl.max-articles-per-request")



}
