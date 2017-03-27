package hackernews.analytics

import akka.NotUsed
import akka.stream.scaladsl.Source
import hackernews.client.{ClientFailure, HackerNewsClient}
import hackernews.core.Comment

import scala.util.Either

class HackerNewsStubbedClient(commentList: List[Comment], failures : List[ClientFailure] = List.empty) extends HackerNewsClient{

  def getCommentsFromTopStories(max : Int) : Source[Either[ClientFailure,Comment],NotUsed] = {
    val rightCommentList : List[Either[ClientFailure,Comment]] = commentList.map {Right(_)}
    val leftFailureList : List[Either[ClientFailure,Comment]] = failures.map {Left(_)}
    Source(leftFailureList ::: rightCommentList)
  }

}
