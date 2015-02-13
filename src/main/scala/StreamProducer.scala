package com.ataraxer.finch

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.actor._

import scala.collection.mutable.Queue


object StreamProducer {
  def props(twitterClient: ActorRef) = {
    Props {
      new StreamProducer(twitterClient)
    }
  }
}


class StreamProducer(twitterClient: ActorRef)
  extends ActorPublisher[TwitterMessage]
{
  import ActorPublisherMessage._

  twitterClient ! TwitterClient.StartUserStream(self)

  private var buffer = Queue.empty[TwitterMessage]


  def receive = {
    case Request(amount) => {
      for (_ <- 0 until amount.toInt if buffer.size > 0) {
        onNext(buffer.dequeue())
      }
    }

    case Cancel =>

    case message: TwitterMessage => {
      if (totalDemand > 0) {
        onNext(message)
      } else {
        buffer.enqueue(message)
      }
    }

    case other => println("Other: " + other)
  }
}


// vim: set ts=2 sw=2 et:
