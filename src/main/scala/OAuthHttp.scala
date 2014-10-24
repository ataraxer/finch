package com.ataraxer.finch

import akka.actor._
import spray.http._
import spray.client.pipelining._


object OAuthHttp {
  def props(http: ActorRef, credentials: OAuth.Credentials) = {
    Props { new OAuthHttp(http, credentials) }
  }
}


class OAuthHttp(http: ActorRef, credentials: OAuth.Credentials)
  extends Actor
{
  val sign = OAuth(credentials).sign _

  def receive = {
    case request: HttpRequest => {
      val signedRequest = request ~> sign
      http forward signedRequest
    }
  }
}


// vim: set ts=2 sw=2 et:
