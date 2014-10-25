package com.ataraxer.finch

import akka.actor._

import spray.http._
import spray.http.HttpMethods._
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
        listener ! StreamMessage(message)
      } else {
        buffer += data
      }
    }

    case other => // TODO: handle unexpected message
  }
}


// vim: set ts=2 sw=2 et:
