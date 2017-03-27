import hackernews.core.{Story, StoryCommented, StoryNotCommented}
import hackernews.protocol.ClientJsonFormat.StoryFormat
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class StoryJsonFormatSpec extends WordSpec with Matchers   {

  "Story Json Format" should  {
    "parse not commented story" in {

      """
        {
           "id" : 13962956,
           "title" : "this is the title",
           "type" : "story"
         }
      """.parseJson.convertTo[Story] should be(StoryNotCommented(13962956,"this is the title"))
    }

    "parse commented story" in {

      """
        {
           "id" : 13962956,
           "title" : "this is the title",
           "kids" : [ 13963442, 13963443],
           "type" : "story"
         }
      """.parseJson.convertTo[Story] should be(StoryCommented(13962956,"this is the title",List(13963442, 13963443)))
    }

    "failed if type is different than story" in {

      val thrown = intercept[DeserializationException]{
      """
        {
            "id" : 13962956,
            "title" : "this is the title",
            "kids" : [ 13963442, 13963443],
            "type" : "unknowm"
         }
      """.parseJson.convertTo[Story]}
      thrown.getMessage should be("""Story expected : {"id":13962956,"title":"this is the title","kids":[13963442,13963443],"type":"unknowm"}""")
    }
  }
}
