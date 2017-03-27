package hackernews.core


case class Comment(id: Int, commenterNameOption:Option[String] = None, repliesIds:List[Int] = List.empty) {
  override def toString : String = { s"${id}-${commenterNameOption} : ${repliesIds}" }

  def isAnonymous = commenterNameOption.isEmpty
  def isNotAnonymous = commenterNameOption.isDefined
  def hasNoReplies = repliesIds.isEmpty
}





