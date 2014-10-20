package com.ataraxer.finch

import org.scalatest._

import spray.http._
import spray.http.HttpMethods._


class OAuthSpec extends UnitSpec {
  import OAuth._

  "OAuth" should "generate OAuth signature" in {
    val consumerKey = KeyPair(
      key = "xvz1evFS4wEEPTGEFPHBog",
      secret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw")

    val userKey = KeyPair(
      key = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb",
      secret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")

    val credentials = Credentials(consumerKey, userKey)

    val extraParams = Map(
      "oauth_nonce" -> "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
      "oauth_timestamp" -> "1318622958")

    val signer = OAuth(credentials, extraParams)

    val host = "https://api.twitter.com"
    val uri = Uri(host + "/1/statuses/update.json?include_entities=true")

    val body = {
      "status=" + encodeUrl("Hello Ladies + Gentlemen, a signed OAuth request!")
    }

    val request = HttpRequest(POST, uri, entity = body)

    val result = signer.signatureFor(request)

    result should be ("tnnArxj06cWHq44gCs1OSKk/jLY=")
  }
}


// vim: set ts=2 sw=2 et:
