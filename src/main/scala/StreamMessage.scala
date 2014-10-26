package com.ataraxer.finch


object StreamMessage {
  case class FriendsMessage(friends: Seq[Long]) {
    override def toString = {
      "FriendsMessage(%d friends)".format(friends.size)
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

  case class UnknownMessage(raw: String)
}


case class StreamMessage(message: String)


// vim: set ts=2 sw=2 et:
