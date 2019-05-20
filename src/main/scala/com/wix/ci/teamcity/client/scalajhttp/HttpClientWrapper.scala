package com.wix.ci.teamcity.client.scalajhttp

import java.nio.charset.StandardCharsets

import com.wix.ci.teamcity.client.HttpClient
import org.apache.commons.codec.binary.Base64
import scalaj.http.Http

class HttpClientWrapper(username: String, password: String, timeout : Int = 5000) extends HttpClient {
  val basicAuth = s"Basic ${createBasicAuthentication()}"
  val authHeaderKey = "Authorization"

  def createBasicAuthentication() : String =
    new String(Base64.encodeBase64((username + ":" + password).getBytes(StandardCharsets.UTF_8)))


  override def executeGet(url : String) : String = {
    execute(url,"GET",None)
  }

  override def executePost(url : String, json : String) : String = {
    execute(url,"POST",Some(json))
  }

  override def executeDelete(url : String) : String = {
    execute(url,"DELETE",None)
  }


  private def execute(url : String, method : String, body : Option[String]) : String ={
    var httpReq = Http(url).method(method)
        .header(authHeaderKey,basicAuth)
        .header("Accept","application/json")
        .header("Content-Type","application/json")
        .timeout(connTimeoutMs = timeout, readTimeoutMs = timeout)
    if(body.isDefined) httpReq = httpReq.postData(body.get)
    val httpResp = httpReq.asString
    checkHttpStatusCode(httpResp.code)
    httpResp.body
  }

  private def checkHttpStatusCode(code : Int) = {
    if(code != 200 && code != 201) throw new RuntimeException(s"Error from server status code ${code}")
  }
}
