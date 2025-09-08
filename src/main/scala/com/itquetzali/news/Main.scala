package com.itquetzali.news

import com.itquetzali.news.etl.{NewsExtractor, NewsLoader, NewsTransformer}
import com.itquetzali.news.util.ConfigLoader
import org.apache.spark.sql.SparkSession

import scala.util.Try

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
object Main {
  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = SparkSession.builder()
      .appName(ConfigLoader.sparkAppName)
      .master(ConfigLoader.sparkMaster)
      .config("spark.sql.shuffle.partitions", 3)
      .getOrCreate()

    val newsLoader = NewsLoader()

    try {
      while(true) {
        println(s"Starting ETL process at ${java.time.Instant.now()}")

        println("Extractiong news articles")
        val rawArticles = NewsExtractor.fetchNewsFromApi()
        println(s"Fetched ${rawArticles.size} articles")

        if(rawArticles.nonEmpty) {
          println("Transforming articles...")
          val transformedArticles = NewsTransformer.transformWithSpark(rawArticles)(spark)
          println(s"Publishing articles to kafka ...")
          newsLoader.loadToKafka(transformedArticles)
          println(s"Successfully processed ${transformedArticles.count()} articles")
        } else {
          println("No articless fetched in this batch")
        }
        println(s"Wating for ${ConfigLoader.batchInterval} minutes until next batch...")
        Thread.sleep(ConfigLoader.batchInterval *60 * 1000)

      }
    }catch {
      case e: Exception =>
        println(s"ETL process failed with error: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      // Clean up resources
      newsLoader.close()
      spark.stop()
    }


    //for (a <- rawArticles) println(a.id)
  }
}

