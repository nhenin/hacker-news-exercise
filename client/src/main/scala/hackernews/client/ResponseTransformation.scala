package hackernews.client

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow

import scala.concurrent.ExecutionContext


trait ResponseTransformation {

  def unmarshallTo[T: FromResponseUnmarshaller](implicit executionContext: ExecutionContext, materializer: Materializer): Flow[HttpResponse, T, NotUsed] =
    Flow[HttpResponse]
      .mapAsync(1)(response => Unmarshal(response).to[T])


}

