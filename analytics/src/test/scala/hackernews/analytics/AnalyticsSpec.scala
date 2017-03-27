package hackernews.analytics

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import hackernews.client.{GetCommentFailed, GetStoryFailed}
import hackernews.core.Comment
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import spray.json.DeserializationException

import scala.concurrent.duration._

class AnalyticsSpec extends TestKit(ActorSystem("AnalyticsSpec"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with ScalaFutures {

  import system.dispatcher
  implicit val materializer = ActorMaterializer()

  override def afterAll {
    materializer.shutdown()
    TestKit.shutdownActorSystem(system)
  }

  "Analytics" should {

    "provide the given number of top commenters with appropriate commenter statistics" in {
       val commentsList = List(
         Comment(id = 1,commenterNameOption = Some("jacques"),repliesIds = List(2)),
         Comment(id = 2,commenterNameOption = Some("paul"),repliesIds = List(3)),
         Comment(id = 3,commenterNameOption = Some("jacques"),repliesIds = List(4)),
         Comment(id = 4,commenterNameOption = Some("jon"),repliesIds = List()),
         Comment(id = 5,commenterNameOption = Some("jacques"),repliesIds = List(6,7)),
         Comment(id = 6,commenterNameOption = Some("bob"),repliesIds = List()),
         Comment(id = 7,commenterNameOption = Some("jon"),repliesIds = List()),
         Comment(id = 8,commenterNameOption = Some("bob"),repliesIds = List()),
         Comment(id = 9,commenterNameOption = Some("jacques"),repliesIds = List()),
         Comment(id = 10,commenterNameOption = Some("bob"),repliesIds = List())
       )

      val client = new HackerNewsStubbedClient(commentsList)
      val sut = new Analytics(client)

      whenReady(sut.getTopCommenters(3,30),timeout(1 seconds)) { topCommenterResult =>
        topCommenterResult shouldBe TopCommenterResult(
          topCommenter = List(
            Commenter("jacques",nbComment = 4,commentingProportionInPercentage = 100*4.0/10),
            Commenter("bob",    nbComment = 3,commentingProportionInPercentage = 100*3.0/10),
            Commenter("jon",    nbComment = 2,commentingProportionInPercentage = 100*2.0/10)
          ),
          nbComments  = 10,
          nbAnonymousComments = 0,
          failures =  List.empty)
      }

    }

    "provide the number of anonymous comments " in {
      val commentsList = List(
        Comment(id = 1,commenterNameOption = Some("jacques"),repliesIds = List(2)),
        Comment(id = 2,commenterNameOption = Some("paul"),repliesIds = List(3)),
        Comment(id = 3,commenterNameOption = Some("jacques"),repliesIds = List(4)),
        Comment(id = 4,commenterNameOption = Some("jon"),repliesIds = List()),
        Comment(id = 5,commenterNameOption = Some("jacques"),repliesIds = List(6,7)),
        Comment(id = 6,commenterNameOption = Some("bob"),repliesIds = List()),
        Comment(id = 7,commenterNameOption = Some("jon"),repliesIds = List()),
        Comment(id = 8,commenterNameOption = Some("bob"),repliesIds = List()),
        Comment(id = 9,commenterNameOption = Some("jacques"),repliesIds = List()),
        Comment(id = 10,commenterNameOption = Some("bob"),repliesIds = List()),
        Comment(id = 11,commenterNameOption = None,repliesIds = List()),
        Comment(id = 12,commenterNameOption = None,repliesIds = List()),
        Comment(id = 13,commenterNameOption = None,repliesIds = List())
      )
      val client = new HackerNewsStubbedClient(commentsList)
      val sut = new Analytics(client)
      val expectedNbAnonymousComments = 3

      whenReady(sut.getTopCommenters(3,30),timeout(1 seconds)) { topCommenterResult =>
        topCommenterResult shouldBe TopCommenterResult(
          topCommenter = List(
            Commenter("jacques",4,100*4.0/13),
            Commenter("bob",3,100*3.0/13),
            Commenter("jon",2,100*2.0/13)
          ),
          nbComments  = 13,
          nbAnonymousComments = expectedNbAnonymousComments,
          failures =  List.empty)
      }

    }

    "provide the list of unrecovered failures " in {
      val commentsList = List()
      val failures = List(GetCommentFailed(1,DeserializationException("Comment expected")),
                          GetCommentFailed(2,DeserializationException("Comment expected")),
                          GetStoryFailed(20,DeserializationException("Story expected")))

      val client = new HackerNewsStubbedClient(commentsList,failures)
      val sut = new Analytics(client)

      whenReady(sut.getTopCommenters(3,30),timeout(1 seconds)) { topCommenterResult =>
        topCommenterResult shouldBe TopCommenterResult(
          topCommenter = List(),
          nbComments  = 0,
          nbAnonymousComments = 0,
          failures =  failures)
      }

    }

  }
}
