package com.itquetzali.news.etl

import com.itquetzali.news.NewsArticle
import org.apache.spark.sql.functions.{regexp_replace, when}
import org.apache.spark.sql.{Dataset, SparkSession}

object NewsTransformer {


  /**
   * normalize text
   */
  def cleanText(text:Option[String]): Option[String] = {
    text.map { t =>
      t.replaceAll("<[^>]+>", "")
        .replaceAll("\\s+", " ")
        .trim()
    }.filter(_.nonEmpty) // Remove HTML tags
  }

  def extractCategories(title: Option[String], content: Option[String]): Option[String] = {
    val keywords = Map(
      "politics" -> Seq("election", "government", "politician", "senate", "congress"),
      "sports" -> Seq("game", "team", "player", "score", "tournament"),
      "technology" -> Seq("tech", "software", "computer", "digital", "ai", "algorithm"),
      "business" -> Seq("market", "economy", "company", "stock", "business", "finance"),
      "health" -> Seq("medical", "health", "disease", "hospital", "medicine")
    )

    val combinedText = (title.getOrElse("") + " " + content.getOrElse("")).toLowerCase

    val matchedCategories = keywords.flatMap { case (category, words) =>
      if (words.exists(word => combinedText.contains(word))) Some(category)
      else None
    }

    if (matchedCategories.nonEmpty) Some(matchedCategories.mkString(","))
    else None
  }

  def transformArticles(articles: List[NewsArticle]): List[NewsArticle] = {
    articles.map { article =>
      article.copy(
        description = cleanText(Option.apply(article.description)).get,
        title = cleanText(Option.apply(article.title)).get,
        category = extractCategories(Some(article.title), article.category)
      )
    }
  }

  // Spark-based transformation for larger datasets
  def transformWithSpark(articles: List[NewsArticle])(implicit spark: SparkSession): Dataset[NewsArticle] = {
    import spark.implicits._

    val articlesDS = articles.toDS()

    articlesDS
      .withColumn("cleanedDescription",
        when($"description".isNotNull, regexp_replace($"description", "<[^>]+>", ""))
          .otherwise($"description"))
      .withColumn("cleanedTitle",
        when($"title".isNotNull, regexp_replace($"title", "<[^>]+>", ""))
          .otherwise($"title"))
      .as[NewsArticle]
  }
}

