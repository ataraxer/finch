package com.ataraxer.finch

import akka.actor._
import spray.http._


object StreamReader {
  def props(listener: ActorRef) = {
    Props { new StreamReader(listener) }
  }

  /**
   * "Empty" chunk of data, sent by stream API to keep connection alive.
   */
  private val HeartbeatMessage = "\r\n"
}


class StreamReader(listener: ActorRef) extends Actor {
  import StreamReader._

  private var buffer: String = ""


  def receive = {
    case ChunkedResponseStart(response) => // TODO: log status

    case MessageChunk(bytes, _) => {
      val data = bytes.asString

      if (data != HeartbeatMessage && data.contains("\r\n")) {
        val message = buffer + data.replace("\r\n", "")
        buffer = ""
        listener ! StreamMessage(message)
      } else {
        buffer += data
      }
    }

    case _: Terminated => context.stop(self)
    case other => context.stop(self)
  }
}


// vim: set ts=2 sw=2 et:
