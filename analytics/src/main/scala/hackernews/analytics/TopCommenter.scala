package hackernews.analytics

import hackernews.client.ClientFailure

case class TopCommenterResult(topCommenter : List[Commenter],
                              nbComments : Int,
                              nbAnonymousComments : Int,
                              failures : List[ClientFailure]) {
  val nbAnonymousCommentsPercentage = (100.0 * nbAnonymousComments.toDouble / nbComments.toDouble)
  val hasFailures = !failures.isEmpty
}

case class Commenter (name:String,nbComment:Int,commentingProportionInPercentage : Double)



