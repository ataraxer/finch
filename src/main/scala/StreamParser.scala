package com.ataraxer.finch

import akka.actor._

import org.json4s._
import org.json4s.native.{Serialization => Json}
import org.json4s.native.JsonMethods._


object StreamParser {
  /* === Constructors ==== */
  def props(listener: ActorRef) = {
    Props { new StreamParser(listener) }
  }

  /* ==== Messages ==== */
  case object StartUserStream
}


class StreamParser(listener: ActorRef) extends Actor {
  import StreamParser._
  import StreamMessage._

  implicit val jsonFormats = Json.formats(NoTypeHints)


  def receive = {
    case StreamMessage(message) => {
      val json = parse(message)

      val result = {
        if (json \ "friends" != JNothing) {
          json.extract[FriendsMessage]
        } else if (json \ "text" != JNothing) {
          json.extract[TweetMessage]
        } else {
          UnknownMessage(message)
        }
      }

      listener ! result
    }
  }
}


// vim: set ts=2 sw=2 et:
