package hackernews.topCommenter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import hackernews.analytics.Analytics
import hackernews.client.HackerNewsHttpClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util._

object TopCommenterApp extends App {


    implicit val system = ActorSystem("TopCommenterApp")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    val analytics = new Analytics(HackerNewsHttpClient())
    val nbTopStories = 30
    val nbTopCommenters = 10
    analytics.getTopCommenters(nbTopCommenters,nbTopStories) onComplete {
        case Success(topCommenterResult) =>
            println("---------------------------------")
            println("Subject : print the title of the top 30 hacker news stories and the top 10 commenter names " +
                    "of these stories with the total number of comments that they posted (only for these 30 stories).")
            println("---------------------------------")
            println(s"> Total comments for ${nbTopStories} top stories : ${topCommenterResult.nbComments}")
            println(s"> Total anonymous comments : ${topCommenterResult.nbAnonymousComments} (${"%1.2f".format(topCommenterResult.nbAnonymousCommentsPercentage)}%)")
            println(s"> Top Commenters : ")
            topCommenterResult.topCommenter.zipWithIndex.foreach{case (commenter,index) =>
                println(s"\t${"%3d".format(index+1)} - ${commenter.name} : ${commenter.nbComment} comments (${"%1.2f".format(commenter.commentingProportionInPercentage)}%)")}
            if(topCommenterResult.hasFailures){
                println("> Hacker news api failures not recoverable : ")
                topCommenterResult.failures.foreach(failure => println(s"${failure}"))
            }
            println("---------------------------------")
            closeApplication()
        case Failure(t) =>
            println("> An error has occured: " + t)
            closeApplication()
    }


    def closeApplication() = {
        val systemTerminatedFuture = Http().shutdownAllConnectionPools().flatMap { _ =>
            materializer.shutdown()
            system.terminate()
        }
        Await.result(systemTerminatedFuture, Duration.Inf)
    }
}






