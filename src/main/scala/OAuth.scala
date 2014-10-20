package com.ataraxer.finch

import spray.http._
import spray.http.HttpHeaders._

import org.apache.commons.codec.binary.Base64

import java.net.{URLEncoder, URLDecoder}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object OAuth {
  case class KeyPair(key: String, secret: String)
  case class Credentials(consumer: KeyPair, token: KeyPair)

  def encodeUrl(input: String) = {
    URLEncoder.encode(input, "UTF-8")
      .replace("+", "%20")
      .replace("%7E", "~")
      .replace("*", "%2A")
  }

  def decodeUrl(input: String) = {
    URLDecoder.decode(input, "UTF-8")
      .replace("%20", "+")
      .replace("~", "%7E")
      .replace("%2A", "*")
  }

  def parseBodyParams(body: String): Seq[(String, String)] = {
    val result = for {
      param <- decodeUrl(body) split '&'
      Array(key, value) <- Some(param split '=')
    } yield key -> value

    result.toSeq
  }


  def buildSignature(components: String*): String = {
    components.map(encodeUrl).mkString("&")
  }


  def hmacSHA1(message: String, key: String): Array[Byte] = {
    val keySpec = new SecretKeySpec(key.getBytes, "HmacSHA1")

    val encoder = Mac.getInstance("HmacSHA1")
    encoder.init(keySpec)

    encoder.doFinal(message.getBytes)
  }


  def base64(message: Array[Byte]): String = {
    Base64.encodeBase64String(message)
  }


  def generateSignature(message: String, key: String): String = {
    val hash = hmacSHA1(message, key)
    println(hash.map(b => "%02X".format(b)).mkString(" "))
    base64(hash)
  }


  def extractRequestParams(request: HttpRequest) = {
    val queryParams = request.uri.query.toList
    val bodyParams = parseBodyParams(request.entity.asString)
    queryParams ++ bodyParams
  }
}


case class OAuth(
    credentials: OAuth.Credentials,
    extraParams: Map[String, String] = Map.empty)
{
  import OAuth._


  def generateOAuthParams = List(
    "oauth_consumer_key" -> credentials.consumer.key,
    "oauth_signature_method" -> "HMAC-SHA1",
    "oauth_timestamp" -> (System.currentTimeMillis / 1000).toString,
    //"oauth_nonce" -> System.nanoTime.toString,
    "oauth_nonce" -> util.Random.alphanumeric.take(32).mkString,
    "oauth_token" -> credentials.token.key,
    "oauth_version" -> "1.0") ++ extraParams


  def signatureFor(
    request: HttpRequest,
    oauthParams: List[(String, String)] = generateOAuthParams) =
  {
    val params = extractRequestParams(request) ++ oauthParams

    val sortedParams = params.toMap.toList sortBy { case (name, _) => name }

    val parameterString = sortedParams map { case (name, value) =>
      name + "=" + encodeUrl(value)
    } mkString "&"

    val signatureBaseString = buildSignature(
      request.method.toString,
      request.uri.copy(query = Uri.Query.Empty).toString,
      parameterString)

    val signingKey = buildSignature(
      credentials.consumer.secret,
      credentials.token.secret)

    generateSignature(signatureBaseString, signingKey)
  }


  def sign(request: HttpRequest) = {
    val oauthParams = generateOAuthParams
    val signature = signatureFor(request, oauthParams)
    val signedParams = oauthParams :+ ("oauth_signature" -> signature)

    val oauthString = signedParams map {
      case (k, v) => "%s=\"%s\"".format(encodeUrl(k), encodeUrl(v))
    } mkString ", "

    val authorizationHeader = RawHeader("Authorization", "OAuth " + oauthString)

    request.withHeaders(authorizationHeader)
  }
}


// vim: set ts=2 sw=2 et:
