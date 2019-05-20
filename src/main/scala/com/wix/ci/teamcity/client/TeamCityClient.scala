package com.wix.ci.teamcity.client

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class TeamCityClient(httpClient : HttpClient, baseUrl : String) {
  val mapper = MapperFactory.createMapper()


  def createProject(project : BaseProject) : BaseProject = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
    val projectJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project)
    val json = httpClient.executePost(url,projectJson)
    mapper.readValue(json,classOf[BaseProject])
  }

  def getProjects : Projects = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
    val json = httpClient.executeGet(url)
    mapper.readValue(json,classOf[Projects])
  }

  def deleteProject(projectId : String) : Unit = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${projectId}"
    httpClient.executeDelete(url)
  }

  def getProjectById(projectId : String) : Project = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${projectId}"
    val json = httpClient.executeGet(url)
    mapper.readValue(json,classOf[Project])
  }

  def getBuildTypes() : BuildTypes = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes"
    val json = httpClient.executeGet(url)
    mapper.readValue(json,classOf[BuildTypes])
  }

  def getBuildTypesByVcsRootId(vcsRootId : String) : BuildTypes ={
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes?locator=vcsRoot:(id:${vcsRootId})"
    val json = httpClient.executeGet(url)
    mapper.readValue(json,classOf[BuildTypes])
  }




  def getBaseUrl() : String = baseUrl

}


object TeamCityClient{
  val contextPrefix = "httpAuth/app/rest"
}

object MapperFactory{
  def createMapper() : ObjectMapper = {
    val objectMapper = new ObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper
  }
}