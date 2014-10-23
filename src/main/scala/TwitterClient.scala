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

import com.typesafe.config.ConfigFactory


object TwitterClient {
  def props(http: ActorRef, listener: ActorRef) = {
    Props { new TwitterClient(http, listener) }
  }

  case object StartUserStream

  val StreamUri = Uri("https://userstream.twitter.com/1.1/user.json")

  private val config = ConfigFactory.load

  val consumerKey = OAuth.KeyPair(
    key = config.getString("finch.consumer.key"),
    secret = config.getString("finch.consumer.secret"))

  val userKey = OAuth.KeyPair(
    key = config.getString("finch.user.key"),
    secret = config.getString("finch.user.secret"))

  val credentials = OAuth.Credentials(consumerKey, userKey)

  val oauthSigner = OAuth(credentials)

  private val HeartbeatMessage = "\r\n"
}


class TwitterClient(http: ActorRef, listener: ActorRef) extends Actor {
  import TwitterClient._

  implicit val jsonFormats = Json.formats(NoTypeHints)

  def startUserStream(): Unit = {
    val streamRequest = HttpRequest(GET, StreamUri)
    val signedRequest = oauthSigner.sign(streamRequest)
    http ! signedRequest
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
