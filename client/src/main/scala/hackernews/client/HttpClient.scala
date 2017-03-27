package hackernews.client

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Get, addHeader}
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.model.headers.Accept
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import hackernews.core._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import hackernews.protocol.ClientJsonFormat._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext
import scala.util.{Either, Left, Right}

trait HackerNewsClient {
  def getCommentsFromTopStories(max : Int) : Source[Either[ClientFailure,Comment],NotUsed]
}


class HackerNewsHttpClient (baseUrl : String)
                       (implicit ec: ExecutionContext, system: ActorSystem, materializer: Materializer)
  extends HackerNewsClient with ResponseTransformation {

  val connection = Http().outgoingConnectionHttps(baseUrl)

  def getCommentsFromTopStories(max : Int) : Source[Either[ClientFailure,Comment],NotUsed] = {
    Source.single(Get("/v0/topstories.json"))
      .map(addHeader(Accept(MediaTypes.`application/json`)))
      .via(connection)
      .via(unmarshallTo[List[Int]])
      .map(_.take(max))
      .flatMapConcat(list => Source(list))
      .flatMapMerge(max,getCommentsFromStory)
  }

  def getCommentsFromStory(storyId: Int) : Source[Either[ClientFailure,Comment],NotUsed] = {
    Source.single(Get(s"/v0/item/${storyId}.json"))
      .map(addHeader(Accept(MediaTypes.`application/json`)))
      .via(connection)
      .via(unmarshallTo[Story])
      .filter(_.isInstanceOf[StoryCommented])
      .map(_.asInstanceOf[StoryCommented])
      .flatMapConcat{story => Source(story.commentIds)
        .flatMapMerge(story.commentIds.size,commentId => getCommentAndHisReplies(commentId = commentId))}
      .recoverWithRetries(1,{ case error  => getCommentsFromStory(storyId)})
  }


  def getCommentAndHisReplies(commentId: Int, retry : Int = 5 ) : Source[Either[ClientFailure,Comment],NotUsed] = {
    Source.single(Get(s"/v0/item/${commentId}.json"))
      .map(addHeader(Accept(MediaTypes.`application/json`)))
      .via(connection)
      .via(unmarshallTo[Comment])
      .flatMapConcat{comment => comment match {
        case comment:Comment if comment.hasNoReplies  =>  Source.single(Right(comment))
        case comment:Comment =>
          Source.single(Right(comment))
            .concat(Source(comment.repliesIds)
              .flatMapMerge(comment.repliesIds.size,commentId => getCommentAndHisReplies(commentId = commentId)))
      }}
      .recoverWithRetries(retry,{ case error  => getCommentAndHisReplies(commentId,retry -1)})
      .recover{case error => Left(GetCommentFailed(commentId,error))}
  }

}

object HackerNewsHttpClient {

  def provide() (implicit ec: ExecutionContext, system: ActorSystem, materializer: Materializer)
  : HackerNewsHttpClient = { new HackerNewsHttpClient("hacker-news.firebaseio.com")}
}