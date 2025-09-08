package com.itquetzali.news.etl

import com.itquetzali.news.NewsArticle
import com.itquetzali.news.util.ConfigLoader
import play.api.libs.json.{JsArray, Json}
import scalaj.http.Http

import scala.util.{Failure, Success, Try}

object NewsExtractor {

  def fetchNewsFromApi(): List[NewsArticle] = {
    val url = s"${ConfigLoader.newsApiBaseUrl}/latest-news"

    Try {
      val response = Http(url)
        .param("language", "en")
        .param("apiKey", ConfigLoader.newsApiKey)
        .asString
      if(response.isSuccess) {
        val json = Json.parse(response.body)
        val articles = (json \ "news").as[JsArray]

        articles.value.flatMap { articleJson =>
          Try {
            NewsArticle(
              id = (articleJson \ "id").as[String],
              title = (articleJson \ "title").as[String],
              description = (articleJson \ "description").as[String],
              url = (articleJson \ "url").as[String],
              author = (articleJson \ "author").as[String],
              image = (articleJson \ "image").as[String],
              category = (articleJson \ "category").asOpt[String],
              published = (articleJson \ "published").as[String]
            )
          }.toOption
        }.toList
      }else {
        println(s"Failed to fetch news: ${response.statusLine} - ${response.body}")
        List.empty[NewsArticle]
      }
    } match {
      case Success(articles) => articles
      case Failure(exception) =>
        println(s"Error fetching news: ${exception.getMessage}")
        List.empty[NewsArticle]
    }
  }

}
