package com.itquetzali.news

case class NewsArticle(id:String, title: String, description:String, url:String, author:String, image:String,
                       category: Option[String], published:String)

object NewsArticle{
  import play.api.libs.json._
  implicit val format: Format[NewsArticle] = new Format[NewsArticle] {

    override def reads(json: JsValue): JsResult[NewsArticle] = JsSuccess(
      NewsArticle(
        id = (json \ "id").as[String],
        title = ( json \ "title").as[String],
        description = (json \ "description").as[String],
        url = (json  \ "url").as[String],
        author = (json \ "author").as[String],
        image = (json \ "image").as[String],
        category= (json \ "category").asOpt[String],
        published = (json \ "published").as[String]
      )
    )

    override def writes(article: NewsArticle): JsValue = Json.obj(
      "id" -> article.id,
      "title" -> article.title,
      "description" -> article.description,
      "url"-> article.url,
      "author" -> article.author,
      "image" -> article.image,
      "category" -> article.category,
      "published" -> article.published

    )
  }
}