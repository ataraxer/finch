package com.ataraxer.finch

import akka.io.IO
import akka.actor._

import spray.can.Http


object FinchMain extends App {
  implicit val system = ActorSystem("finch-app")

  val twitterClient = system actorOf {
    TwitterClient.props(IO(Http))
  }

  twitterClient ! TwitterClient.StartUserStream
}


// vim: set ts=2 sw=2 et:
