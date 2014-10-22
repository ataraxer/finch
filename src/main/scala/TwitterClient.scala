package com.ataraxer.finch

import akka.actor._

import spray.can._
import spray.http._
import spray.http.HttpMethods._
import spray.http.HttpHeaders._
import spray.client.pipelining._

import com.typesafe.config.ConfigFactory


object TwitterClient {
  def props(http: ActorRef) = {
    Props { new TwitterClient(http) }
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
}


class TwitterClient(http: ActorRef) extends Actor {
  import TwitterClient._


  def startUserStream(): Unit = {
    val streamRequest = HttpRequest(GET, StreamUri)
    val signedRequest = oauthSigner.sign(streamRequest)
    http ! signedRequest
  }


  def receive = {
    case StartUserStream => startUserStream()
    case message => println(message)
  }
}


// vim: set ts=2 sw=2 et:
