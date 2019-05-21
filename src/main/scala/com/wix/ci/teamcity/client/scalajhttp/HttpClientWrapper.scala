package com.wix.ci.teamcity.client.scalajhttp

import java.nio.charset.StandardCharsets

import com.wix.ci.teamcity.client.HttpClient
import org.apache.commons.codec.binary.Base64
import scalaj.http.Http

class HttpClientWrapper(username: String, password: String, timeout: Int = 5000) extends HttpClient {
  val basicAuth = s"Basic ${createBasicAuthentication()}"
  val authHeaderKey = "Authorization"

  def createBasicAuthentication(): String =
    new String(Base64.encodeBase64((username + ":" + password).getBytes(StandardCharsets.UTF_8)))


  override def executeGet(url: String): String = {
    execute(url, "GET", None)
  }

  override def executePost(url: String, json: String): String = {
    execute(url, "POST", Some(json))
  }

  override def executePutPlainText(url: String, body: String): String = {
    execute(url, "PUT", Some(body), "text/plain", "text/plain")
  }

  override def executeDelete(url: String): String = {
    execute(url, "DELETE", None)
  }

  override def executePut(url : String, body : String) : String = {
    execute(url, "PUT", Some(body))
  }




  private def execute(url: String, method: String, body: Option[String], contentType: String = "application/json",accept : String = "application/json"): String = {
    var httpReq = Http(url).method(method)
      .header(authHeaderKey, basicAuth)
      .header("Accept", accept)
      .header("Content-Type", contentType)
      .timeout(connTimeoutMs = timeout, readTimeoutMs = timeout)
    if(body.isDefined && method=="POST") httpReq = httpReq.postData(body.get)
    if(body.isDefined && method=="PUT")httpReq = httpReq.put(body.get)
    val httpResp = httpReq.asString
    checkHttpStatusCode(httpResp.code,httpResp.body)
    httpResp.body
  }

  private def checkHttpStatusCode(code: Int, msg : String) = {
    if (code != 200 && code != 201 && code != 204) throw new RuntimeException(s"Error from server status code $code $msg")
  }
}
