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


  val flowGraph = FlowGraph { implicit builder =>
    import FlowGraphImplicits._

    val messageSource = PropsSource(StreamProducer.props(twitterClient))
    val messageBroadcast = Broadcast[StreamMessage]
    val messageParser = Flow[StreamMessage] map { TwitterMessage(_) }
    def printer = Sink.foreach(println)

    messageSource ~> messageBroadcast ~> messageParser ~> printer
                     messageBroadcast ~> printer
  }


  flowGraph.run()
}


// vim: set ts=2 sw=2 et:
