package com.wix.ci.teamcity.client

trait HttpClient {
  def executeGet(url: String): String

  def executePost(url: String, body: String): String

  def executePutPlainText(url: String, body: String,accept : String = "text/plain"): String

  def executeDelete(url: String): String

  def executePut(url : String, body : String) : String
}
