package com.itquetzali.news

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import play.api.libs.json.Json

import java.util.Properties

class KafkaProducerWrapper(bootstrapService:String) {

  private val props = new Properties()

  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapService)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.ACKS_CONFIG, "1")
  props.put(ProducerConfig.RETRIES_CONFIG, "3")
  private val producer = new KafkaProducer[String, String](props)

  def send(topic: String, key: String, value: String): Unit = {
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record)
  }

  def sendArticle(topic: String, article: NewsArticle): Unit = {
    val jsonValue = Json.stringify(Json.toJson(article))
    println(s"to publish ${jsonValue}")
    send(topic, article.id, jsonValue)
  }

  def close(): Unit = {
    producer.close()
  }
}

object KafkaProducerWrapper {
  def apply(bootstrapServers: String): KafkaProducerWrapper = {
    new KafkaProducerWrapper(bootstrapServers)
  }

}
