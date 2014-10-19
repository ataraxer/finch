package com.ataraxer.tweetstream

import akka.io.IO
import akka.actor._

import spray.can.Http


object TweetStream extends App {
  implicit val system = ActorSystem("tweetstream-app")

  val twitterClient = system actorOf {
    TwitterClient.props(IO(Http))
  }

  twitterClient ! TwitterClient.StartUserStream
}


// vim: set ts=2 sw=2 et:
