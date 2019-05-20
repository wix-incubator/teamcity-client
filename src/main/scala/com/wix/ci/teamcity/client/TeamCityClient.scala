package com.wix.ci.teamcity.client

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class TeamCityClient(httpClient: HttpClient, baseUrl: String) {
  val mapper = MapperFactory.createMapper()


  def createProject(project: BaseProject): BaseProject = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
    val projectJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project)
    val json = httpClient.executePost(url, projectJson)
    if(project.description.isDefined) setProjectDescription(project.id,project.description.get)
    mapper.readValue(json, classOf[BaseProject])
  }

  def setProjectDescription(projectId : String,desc: String) = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:$projectId/description"
    httpClient.executePutPlainText(url,desc)
  }

  def getProjects: Projects = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Projects])
  }

  def deleteProject(projectId: String): Unit = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${projectId}"
    httpClient.executeDelete(url)
  }

  def getProjectById(projectId: String): Project = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${projectId}"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Project])
  }

  def setProjectName(projectId: String, newProjectName: String): Unit = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${projectId}/name"
    httpClient.executePutPlainText(url, newProjectName)
  }

  def getBuildTypes(): BuildTypes = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildTypes])
  }

  def getBuildTypesByVcsRootId(vcsRootId: String): BuildTypes = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes?locator=vcsRoot:(id:${vcsRootId})"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildTypes])
  }

  def createBuildType(baseBuildType: BaseBuildType, projectId: String): BaseBuildType = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${projectId}/buildTypes"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(baseBuildType))
    mapper.readValue(json, classOf[BaseBuildType])
  }

  def getVcsRoot(): Seq[BaseVcsRoot] = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoots]).vcsRoots
  }

  def getVcsRootById(vcsRootId: String): VcsRoot = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoot])
  }


  def getBaseUrl(): String = baseUrl

}


object TeamCityClient {
  val contextPrefix = "httpAuth/app/rest"
}

object MapperFactory {
  def createMapper(): ObjectMapper = {
    val objectMapper = new ObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper
  }
}