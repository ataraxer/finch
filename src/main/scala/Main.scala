package com.ataraxer.finch

import akka.io.IO
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import spray.can.Http

import com.typesafe.config.ConfigFactory


object FinchMain extends App {
  implicit val system = ActorSystem("finch-app")
  implicit val flowBuilder = ActorFlowMaterializer()

  val printer = system.actorOf(Props[Printer])

  val config = ConfigFactory.load

  val consumerKey = OAuth.KeyPair(
    key = config.getString("finch.consumer.key"),
    secret = config.getString("finch.consumer.secret"))

  val userKey = OAuth.KeyPair(
    key = config.getString("finch.user.key"),
    secret = config.getString("finch.user.secret"))

  val credentials = OAuth.Credentials(consumerKey, userKey)

  val oauth = OAuth(credentials)

  val http = system actorOf OAuthHttp.props(IO(Http), credentials)
  val twitterClient = system actorOf TwitterClient.props(http)

  val tweetSource = PropsSource(StreamProducer.props(twitterClient))

  tweetSource.runForeach(println)
}


class Printer extends Actor {
  def receive = {
    case message => println(message)
  }
}


// vim: set ts=2 sw=2 et:
