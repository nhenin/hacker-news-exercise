package hackernews.client


sealed trait ClientFailure

case class GetCommentFailed(commentId : Int, error : Throwable) extends ClientFailure
case class GetStoryFailed(commentId : Int, error : Throwable) extends ClientFailure

