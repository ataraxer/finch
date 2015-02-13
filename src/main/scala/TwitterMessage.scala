package com.ataraxer.finch

import org.json4s._
import org.json4s.native.{Serialization => Json}
import org.json4s.native.JsonMethods._


trait TwitterMessage


object TwitterMessage {
  case class FriendsMessage(friends: Seq[Long]) extends TwitterMessage {
    override def toString = {
      "FriendsMessage(%d friends)".format(friends.size)
    }
  }


  private implicit val jsonFormats = Json.formats(NoTypeHints)


  def apply(message: StreamMessage) = {
      val json = parse(message.content)

      if (json \ "friends" != JNothing) {
        json.extract[FriendsMessage]
      } else if (json \ "text" != JNothing) {
        json.extract[TweetMessage]
      } else {
        UnknownMessage(message.content)
      }
  }


  case class TweetMessage(
      id: Long,
      favorited: Boolean,
      favorite_count: Int,
      retweeted: Boolean,
      retweet_count: Int,
      user: User,
      place: Place,
      lang: String,
      text: String,
      entities: Option[Entities])
    extends TwitterMessage
  {
    def favoriteCount = favorite_count
    def retweetCount = retweet_count

    override def toString = {
      "@%s: %s".format(user.screenName, text)
    }
  }


  case class Entities(
    hashtags: Seq[String],
    urls: Seq[String],
    user_mentions: Seq[String])
  {
    def userMentions = user_mentions
  }


  case class User(name: String, screen_name: String) {
    def screenName = screen_name
  }


  case class Place(country: String, name: String)

  case class UnknownMessage(raw: String) extends TwitterMessage
}


// vim: set ts=2 sw=2 et:
