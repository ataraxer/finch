package com.ataraxer.finch

import akka.actor._

import spray.http._
import spray.http.HttpMethods._
import spray.client.pipelining._


object TwitterClient {
  /* === Constructors ==== */
  def props(http: ActorRef) = {
    Props { new TwitterClient(http) }
  }

  /* ==== Messages ==== */
  case class StartUserStream(listener: ActorRef)

  /**
   * Twitter user stream API endpoint.
   */
  private val StreamUri = Uri("https://userstream.twitter.com/1.1/user.json")
}


class TwitterClient(http: ActorRef) extends Actor {
  import TwitterClient._


  def receive = {
    case StartUserStream(listener: ActorRef) => {
      val reader = context actorOf StreamReader.props(listener)
      http.tell(HttpRequest(GET, StreamUri), reader)
    }

    case other => // TODO: handle unexpected message
  }
}


// vim: set ts=2 sw=2 et:
