package com.ataraxer.finch

import akka.io.IO
import akka.actor._

import spray.can.Http


object FinchMain extends App {
  implicit val system = ActorSystem("finch-app")

  val printer = system.actorOf(Props[Printer])

  val twitterClient = system actorOf {
    TwitterClient.props(IO(Http), printer)
  }

  twitterClient ! TwitterClient.StartUserStream
}


class Printer extends Actor {
  def receive = {
    case message => println(message)
  }
}


// vim: set ts=2 sw=2 et:
