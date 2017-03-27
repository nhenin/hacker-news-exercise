package hackernews.core

sealed trait Story
case class StoryCommented(id: Int, title:String, commentIds:List[Int]) extends Story
case class StoryNotCommented(id: Int, title:String) extends Story

