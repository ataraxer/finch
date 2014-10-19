package com.ataraxer.tweetstream

import akka.actor._

import spray.http._
import spray.http.HttpMethods._
import spray.http.HttpHeaders._


object TwitterClient {
  def props(http: ActorRef) = {
    Props { new TwitterClient(http) }
  }

  case object StartUserStream

  var StreamUri = Uri("https://userstream.twitter.com/1.1/user.json")
}


class TwitterClient(http: ActorRef) extends Actor {
  import TwitterClient._

  def startUserStream(): Unit = {
    val headers = List {
      RawHeader("Authorization", "None")
    }

    val streamRequest = HttpRequest(GET, StreamUri, headers)

    http ! streamRequest
  }


  def receive = {
    case StartUserStream => startUserStream()
    case message => println(message)
  }
}





// vim: set ts=2 sw=2 et:
