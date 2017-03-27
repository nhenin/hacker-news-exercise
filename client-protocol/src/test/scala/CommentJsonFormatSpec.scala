import hackernews.core.Comment
import org.scalatest.{Inside, Matchers, WordSpec}
import spray.json._
import hackernews.protocol.ClientJsonFormat.CommentFormat

class CommentJsonFormatSpec extends WordSpec with Matchers   {

  "Comment Json Format" should  {
    "parse anonymous comment without replies" in {

      """
        {
           "id" : 13962956,
           "type" : "comment"
         }
      """.parseJson.convertTo[Comment] should be(Comment(13962956))
    }

    "parse anonymous comment with replies" in {

      """
        {
           "id" : 13962956,
           "kids" : [ 13963442, 13963443],
           "type" : "comment"
         }
      """.parseJson.convertTo[Comment] should be(Comment(13962956,None,List(13963442, 13963443)))
    }

    "parse comment with replies" in {

      """
        {
           "id" : 13962956,
           "by" : "jacques",
           "kids" : [ 13963442, 13963443],
           "type" : "comment"
         }
      """.parseJson.convertTo[Comment] should be(Comment(13962956,Some("jacques"),List(13963442, 13963443)))
    }


    "parse comment without replies" in {
      """
        {
           "id" : 13962956,
           "by" : "jacques",
           "type" : "comment"
         }
      """.parseJson.convertTo[Comment] should be(Comment(13962956,Some("jacques")))
    }

    "failed if type is different than comment" in {

      val thrown = intercept[DeserializationException]{
      """
        {
           "id" : 13962956,
           "by" : "jacques",
           "type" : "unknowm"
         }
      """.parseJson.convertTo[Comment]}
      thrown.getMessage should be("""Comment expected : {"id":13962956,"by":"jacques","type":"unknowm"}""")
    }
  }
}
