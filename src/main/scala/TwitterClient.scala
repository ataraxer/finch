package com.ataraxer.finch

import akka.actor._

import org.json4s._
import org.json4s.native.{Serialization => Json}
import org.json4s.native.JsonMethods._

import spray.can._
import spray.http._
import spray.http.HttpMethods._
import spray.http.HttpHeaders._
import spray.client.pipelining._


object TwitterClient {
  /* === Constructors ==== */
  def props(http: ActorRef, listener: ActorRef) = {
    Props { new TwitterClient(http, listener) }
  }

  /* ==== Messages ==== */
  case object StartUserStream

  /**
   * Twitter user stream API endpoint.
   */
  private val StreamUri = Uri("https://userstream.twitter.com/1.1/user.json")

  /**
   * "Empty" chunk of data, sent by stream API to keep connection alive.
   */
  private val HeartbeatMessage = "\r\n"
}


class TwitterClient(http: ActorRef, listener: ActorRef) extends Actor {
  import TwitterClient._

  implicit val jsonFormats = Json.formats(NoTypeHints)

  def startUserStream(): Unit = {
    http ! HttpRequest(GET, StreamUri)
  }


  private var buffer: String = ""

  def receive = {
    case StartUserStream => startUserStream()
    case ChunkedResponseStart(response) => // TODO: log status

    case MessageChunk(bytes, _) => {
      val data = bytes.asString

      if (data != HeartbeatMessage && data.contains("\r\n")) {
        val message = buffer + data
        buffer = ""
        val json = parse(message)

        val result = {
          if (json \ "friends" != JNothing) {
            json.extract[StreamMessage.FriendsMessage]
          } else if (json \ "text" != JNothing) {
            json.extract[StreamMessage.TweetMessage]
          } else {
            StreamMessage.UnknownMessage(message)
          }
        }

        listener ! result
      } else {
        buffer += data
      }
    }

    case other => // TODO: handle unexpected message
  }
}


// vim: set ts=2 sw=2 et:
