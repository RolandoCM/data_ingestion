package com.itquetzali.news.etl

import com.itquetzali.news.util.ConfigLoader
import com.itquetzali.news.{KafkaProducerWrapper, NewsArticle}
import org.apache.spark.sql.{DataFrame, Dataset}


class NewsLoader(kafkaProducer: KafkaProducerWrapper, topic: String) {

  def loadToKafka(articles: List[NewsArticle]): Unit = {
    articles.foreach { article =>
      try {
        kafkaProducer.sendArticle(topic, article)
        println(s"Successfully published article: ${article.title}")
      } catch {
        case e: Exception =>
          println(s"Failed to publish article ${article.title}: ${e.getMessage}")
      }
    }
  }
  def loadToKafka(articles: Dataset[NewsArticle]): Unit = {
    articles.collect().foreach{
      article =>
        try {

          kafkaProducer.sendArticle(topic, article)
          println(s"Successfully published article: ${article.title}")
        } catch {
          case e: Exception =>
            println(s"Failed to publish article ${article.title}: ${e.getMessage}")
        }
    }
  }

  def close(): Unit = {
    kafkaProducer.close()
  }
}

object NewsLoader {
  def apply(): NewsLoader = {
    val producer = KafkaProducerWrapper(ConfigLoader.kafkaBootstrapServers)
    new NewsLoader(producer, ConfigLoader.kafkaTopic)
  }
}
