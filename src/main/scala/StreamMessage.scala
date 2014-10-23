package com.ataraxer.finch


object StreamMessage {
  case class FriendsMessage(friends: Seq[Long])

  case class TweetMessage(
    id: Long,
    favorited: Boolean,
    retweeted: Boolean,
    lang: String,
    text: String)

  case class UnknownMessage(raw: String)
}


// vim: set ts=2 sw=2 et:
