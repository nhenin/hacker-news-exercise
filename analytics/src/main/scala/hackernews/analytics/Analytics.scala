package hackernews.analytics

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import hackernews.client.{HackerNewsClient}

import scala.concurrent.{ExecutionContext, Future}

class Analytics(client: HackerNewsClient)
               (implicit ec: ExecutionContext, system: ActorSystem, materializer: Materializer) {

  def getTopCommenters(nbCommenter: Int, nbTopStories: Int): Future[TopCommenterResult] = {
    client.getCommentsFromTopStories(nbTopStories)
      .runWith(Sink.seq)
      .map { commentsFromTopStoriesResult =>
        val failures = commentsFromTopStoriesResult
          .filter(_.isLeft).map { _.left.get }
          .toList
        val totalCommentsNumber = commentsFromTopStoriesResult.size - failures.size
        val nbAnonymousComments = commentsFromTopStoriesResult
          .filter(_.isRight).map { _.right.get }
          .filter(_.isAnonymous).size
        val topCommenters = commentsFromTopStoriesResult
          .filter(_.isRight).map { _.right.get }
          .filter(_.isNotAnonymous)
          .groupBy(comment => comment.commenterNameOption)
          .map { case (commenterNameOption, associatedComments) =>
            val nbComments = associatedComments.size
            val commentingProportion = (100.0 * nbComments) / totalCommentsNumber
            Commenter(commenterNameOption.get, associatedComments.size, commentingProportion)}
          .toList
          .sortBy(_.nbComment)
          .reverse
          .take(nbCommenter)

        TopCommenterResult(
          topCommenters,
          nbComments = totalCommentsNumber,
          nbAnonymousComments = nbAnonymousComments,
          failures)
      }

  }

}

