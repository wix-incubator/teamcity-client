package com.wix.ci.teamcity.client

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class TeamCityClient(httpClient: HttpClient, baseUrl: String) {
  val mapper = MapperFactory.createMapper()
  val rootProjectId = "_Root"
  val rootProjectName = "<Root project>"

  def createProject(project: BaseProject): BaseProject = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects"
    val projectJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project)
    val json = httpClient.executePost(url, projectJson)
    if (project.description.isDefined) setProjectDescription(project.id, project.description.get)
    setProjectArchived(project.id, project.archived)
    mapper.readValue(json, classOf[BaseProject])
  }

  def setProjectDescription(projectId: String, desc: String): Unit = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:$projectId/description"
    httpClient.executePutPlainText(url, desc)
  }

  def setProjectArchived(projectId: String, archived: Boolean): Unit = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:$projectId/archived"
    httpClient.executePutPlainText(url, archived.toString)
  }

  def setProjectName(projectId: String, newProjectName: String): Unit = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:${projectId}/name"
    httpClient.executePutPlainText(url, newProjectName)
  }

  def getProjects: Projects = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Projects])
  }

  def deleteProject(projectId: String): Unit = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:${projectId}"
    httpClient.executeDelete(url)
  }

  def getProjectById(projectId: String): Project = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:${projectId}"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Project])
  }


  def getBuildTypes(): BuildTypes = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildTypes])
  }

  def getBuildTypesByVcsRootId(vcsRootId: String): BuildTypes = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes?locator=vcsRoot:(id:${vcsRootId})"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildTypes])
  }

  def createBuildType(baseBuildType: BaseBuildType): BaseBuildType = {
    val url = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseBuildType.projectId}/buildTypes"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(baseBuildType))
    mapper.readValue(json, classOf[BaseBuildType])
  }

  def createVcsRoot(vcsRoot: VcsRoot): BaseVcsRoot = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/vcs-roots"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(vcsRoot))
    setVcsRootProperties(vcsRoot.id, vcsRoot.properties.property)
    mapper.readValue(json, classOf[BaseVcsRoot])
  }

  def setVcsRootProperties(vcsRootId: String, properties: List[Property]) = {
    properties foreach (p => {
      val url = s"$baseUrl/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId/properties/${p.name}"
      httpClient.executePutPlainText(url, p.value)
    })
  }

  def getVcsRoots(): VcsRoots = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/vcs-roots"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoots])
  }

  def getVcsRootById(vcsRootId: String): VcsRoot = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoot])
  }

  def deleteVcsRoot(vcsRootId: String): Unit = {
    val url = s"$baseUrl/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId"
    httpClient.executeDelete(url)
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