package hackernews.protocol

import hackernews.core._
import spray.json.{DeserializationException, JsArray, JsNumber, JsString, JsValue, RootJsonFormat, RootJsonReader}

object ClientJsonFormat extends ClientJsonFormat

trait ClientJsonFormat {
  import spray.json.DefaultJsonProtocol._
  implicit object StoryFormat extends RootJsonReader[Story] {
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "title", "kids","type") match {
        case Seq(JsNumber(id), JsString(title), JsArray(kids), JsString(objectType)) if objectType == "story"  =>  StoryCommented(id.toInt, title, kids.toList.map(_.convertTo[Int]) )
        case Seq(JsNumber(id), JsString(title), JsString(objectType)) if objectType == "story" =>  StoryNotCommented(id.toInt, title)
        case _ => throw new DeserializationException(s"Story expected : ${value.toString()}")
      }
    }
  }

  implicit object CommentFormat extends RootJsonReader[Comment] {
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "by", "kids","type") match {
        case Seq(JsNumber(id), JsString(by), JsArray(kids), JsString(objectType)) if objectType == "comment" => Comment(id.toInt, Some(by), kids.toList.map(_.convertTo[Int]) )
        case Seq(JsNumber(id), JsString(by), JsString(objectType)) if objectType == "comment" =>  Comment(id.toInt, Some(by))
        case Seq(JsNumber(id), JsString(objectType)) if objectType == "comment" =>  Comment(id.toInt)
        case Seq(JsNumber(id), JsArray(kids), JsString(objectType)) if objectType == "comment" =>  Comment(id.toInt,None,kids.toList.map(_.convertTo[Int]) )
        case _ => throw new DeserializationException(s"Comment expected : ${value.toString()}")
      }
    }
  }
}
