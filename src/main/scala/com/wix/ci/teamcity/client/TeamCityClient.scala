package com.wix.ci.teamcity.client

import java.net.URLEncoder

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.wix.ci.teamcity.client.TeamCityClient._
import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper

class TeamCityClient(httpClient: HttpClient, baseUrl: String) {
  val mapper = MapperFactory.createMapper()
  val rootProjectId = "_Root"
  val rootProjectName = "<Root project>"

  def createProject(project: BaseProject): BaseProject = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects"
    val projectJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project)
    val json = httpClient.executePost(url, projectJson)
    if (project.description.isDefined) setProjectDescription(project.id, project.description.get)
    setProjectArchived(project.id, project.archived)
    mapper.readValue(json, classOf[BaseProject])
  }

  def moveProject(projectId: String, newParentProject: BaseProject): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/$projectId/parentProject"
    httpClient.executePut(url, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newParentProject))
  }

  def setProjectDescription(projectId: String, desc: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:$projectId/description"
    httpClient.executePutPlainText(url, desc, acceptTextPlain)
  }

  def setProjectArchived(projectId: String, archived: Boolean): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:$projectId/archived"
    httpClient.executePutPlainText(url, archived.toString, acceptTextPlain)
  }

  def setProjectName(projectId: String, newProjectName: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:$projectId/name"
    httpClient.executePutPlainText(url, newProjectName, acceptTextPlain)
  }

  def getProjects: Projects = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Projects])
  }

  def deleteProject(projectId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:$projectId"
    httpClient.executeDelete(url)
  }

  def getProjectByName(projectName: String): Project = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/name:$projectName"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Project])
  }

  def getProjectById(projectId: String): Project = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:$projectId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Project])
  }

  def getBuildType(buildTypeId: String): BuildType = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildType])
  }

  def getBuildTypeByName(buildTypeName: String): BuildType = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/name:${ escape(buildTypeName) }"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildType])
  }

  def getVcsRootsByProjectId(projectId: String): VcsRoots = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots?locator=project:(id:${ projectId })"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoots])
  }

  def getBuildTypes(): BuildTypes = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildTypes])
  }

  def getBuildTypesByVcsRootId(vcsRootId: String): BuildTypes = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes?locator=vcsRoot:(id:$vcsRootId)"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BuildTypes])
  }

  def createBuildType(baseBuildType: BaseBuildType): BaseBuildType = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:${ baseBuildType.projectId }/buildTypes"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(baseBuildType))
    mapper.readValue(json, classOf[BaseBuildType])
  }

  def deleteBuildType(buildTypeId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId"
    httpClient.executeDelete(url)
  }

  def createVcsRoot(vcsRoot: VcsRoot): BaseVcsRoot = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(vcsRoot))
    setVcsRootProperties(vcsRoot.id, vcsRoot.properties.property)
    mapper.readValue(json, classOf[BaseVcsRoot])
  }

  def setVcsRootUrl(vcsRootId: String, vcsUrl: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots/$vcsRootId/properties/url"
    httpClient.executePutPlainText(url, vcsUrl, acceptTextPlain)
  }

  def setVcsRootProperties(vcsRootId: String, properties: List[Property]) = {
    properties foreach (p => {
      val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots/id:$vcsRootId/properties/${ p.name }"
      httpClient.executePutPlainText(url, p.value, acceptTextPlain)
    })
  }

  def getVcsRoots(): VcsRoots = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoots])
  }

  def getVcsRootById(vcsRootId: String): VcsRoot = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots/id:$vcsRootId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoot])
  }

  def getVcsRootByName(vcsRootName: String): VcsRoot = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots/name:${ escape(vcsRootName) }"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoot])
  }

  def getVcsRootByUrl(vcsUrl: String): VcsRoot = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots?locator=property:(name:url,value:$vcsUrl)"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[VcsRoot])
  }

  def deleteVcsRoot(vcsRootId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/vcs-roots/id:$vcsRootId"
    httpClient.executeDelete(url)
  }

  def setBuildTypeVcsRootEntries(buildTypeId: String, vcsRootEntries: VcsRootEntries): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/$buildTypeId/vcs-root-entries"
    httpClient.executePut(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(vcsRootEntries))
  }

  def getTeamCityServerDetails(): TeamCityServerDetails = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/server"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[TeamCityServerDetails])
  }

  def createTemplate(template: BaseTemplate): Template = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/projects/id:${ template.projectId }/templates"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(template))
    mapper.readValue(json, classOf[Template])
  }

  def getTemplates(): Templates = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes?locator=templateFlag:true"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Templates])
  }

  def deleteTemplate(templateId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$templateId"
    httpClient.executeDelete(url)
  }

  def attachTemplateToBuildType(templateId: String, buildTypeId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/$buildTypeId/template"
    httpClient.executePutPlainText(url, s"id:$templateId", acceptApplicationJson)
  }

  def detachTemplateToBuildType(buildTypeId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/$buildTypeId/template"
    httpClient.executeDelete(url)
  }

  def setSnapshotDependency(buildTypeId: String, dependency: SnapshotDependency): SnapshotDependency = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/snapshot-dependencies"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(dependency))
    mapper.readValue(json, classOf[SnapshotDependency])
  }

  def getSnapShotDependencies(buildTypeId: String): SnapshotDependencies = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/snapshot-dependencies"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[SnapshotDependencies])
  }

  def deleteSnapshotDependency(buildTypeId: String, snapshotDependencyId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/snapshot-dependencies/$snapshotDependencyId"
    httpClient.executeDelete(url)
  }

  def addBuildStepToBuildType(buildTypeId: String, step: Step): Step = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/steps"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(step))
    mapper.readValue(json, classOf[Step])
  }

  def addBuildParameterToBuildType(buildTypeId: String, paramName: String, value: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/$buildTypeId/parameters/$paramName"
    httpClient.executePutPlainText(url, value, acceptTextPlain)
  }

  def deleteBuildParameter(buildTypeId: String, paramName: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/$buildTypeId/parameters/$paramName"
    httpClient.executeDelete(url)
  }

  def pauseBuild(buildTypeId: String, pause: Boolean): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/$buildTypeId/paused"
    httpClient.executePutPlainText(url, pause.toString, acceptTextPlain)
  }

  def getBuildSteps(buildTypeId: String): Steps = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/steps"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Steps])
  }

  def deleteBuildStep(buildTypeId: String, stepId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/steps/$stepId"
    httpClient.executeDelete(url)
  }

  def addTriggerToBuildType(buildTypeId: String, trigger: Trigger): Trigger = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/triggers"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(trigger))
    mapper.readValue(json, classOf[Trigger])
  }

  def deleteTriggerFromBuildType(buildTypeId: String, triggerId: String): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/triggers/$triggerId"
    httpClient.executeDelete(url)
  }

  def createUser(user: BaseUser): BaseUser = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/users"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(user))
    mapper.readValue(json, classOf[BaseUser])
  }

  def getUsers(): Users = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/users"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Users])
  }

  def getUserById(userId: Long): User = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/users/id:$userId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[User])
  }

  def deleteUser(userId: Long): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/users/id:$userId"
    httpClient.executeDelete(url)
  }

  def getAgents(): Agents = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents?locator=authorized:any"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Agents])
  }

  def getAuthorizedAgents(): Agents = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents?locator=authorized:true"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Agents])
  }

  def getAgentById(agentId: Long): Agent = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents/id:$agentId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Agent])
  }

  def authorizeAgent(agentId: Long, authorize: Boolean): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents/id:$agentId/authorized"
    httpClient.executePutPlainText(url, authorize.toString, acceptTextPlain)
  }

  def setAgentEnabled(agentId: Long, isEnable: Boolean): Unit = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents/id:$agentId/enabled"
    httpClient.executePutPlainText(url, isEnable.toString, acceptTextPlain)
  }

  def addToQueue(buildTypeId: String,
                 properties: Option[Properties],
                 comment: Option[Comment] = None,
                 branch: Option[String] = None): Build = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildQueue"
    val build = Build(buildTypeId, properties, comment)
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(build))
    mapper.readValue(json, classOf[Build])
  }

  def getBuildsInQueue(): Builds = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildQueue"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Builds])
  }

  def getRunningBuilds(): Builds = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/builds?locator=running:true"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Builds])
  }

  def getBuild(buildId: String): Build = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/builds/id:$buildId"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[Build])
  }

  def getLastBuildByStatus(buildTypeId: String, status: String): BaseBuild = {
    val url = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/builds/status:$status?count=1"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[BaseBuild])
  }

  def getAgentsPools(): AgentPools = {
    val url = s"$baseUrl/$contextPrefix/agentPools"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[AgentPools])
  }

  def addAgentPool(agentPool: BaseAgentPool) = {
    val url = s"$baseUrl/$contextPrefix/agentPools"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(agentPool))
    mapper.readValue(json, classOf[AgentPool])
  }

  def getAgentsPoolWithId(id: Int): AgentPool = {
    val url = s"$baseUrl/$contextPrefix/agentPools/id:$id"
    val json = httpClient.executeGet(url)
    mapper.readValue(json, classOf[AgentPool])
  }

  def deleteAgentPool(id: Int): Unit = {
    val url = s"$baseUrl/$contextPrefix/agentPools/id:$id"
    httpClient.executeDelete(url)
  }

  def addProjectToPool(project: BaseProject, agentPoolId: Int): Project = {
    val url = s"$baseUrl/$contextPrefix/agentPools/id:$agentPoolId/projects"
    val json = httpClient.executePost(url, mapper.writerWithDefaultPrettyPrinter.writeValueAsString(project))
    mapper.readValue(json, classOf[Project])
  }

  def deleteProjectFromPool(agentPoolId: Int, projectId: String): Unit = {
    val url = s"$baseUrl/$contextPrefix/agentPools/id:$agentPoolId/projects/id:$projectId"
    httpClient.executeDelete(url)
  }

  def moveAgentFromPool(agentId: Int, poolId: Int): AgentPool = {
    val url = s"$baseUrl/$contextPrefix/agentPools/id:$poolId/agents"
    val json = httpClient.executePost(url, s"""{"id":"$agentId"}""")
    mapper.readValue(json, classOf[AgentPool])
  }


  def getBaseUrl: String = baseUrl

  private def escape(param: String): String =
    URLEncoder.encode(param, "UTF-8").replaceAll("\\+", "%20")
}


object TeamCityClient {
  val contextPrefix = "httpAuth/app/rest"
  val acceptTextPlain = "text/plain"
  val acceptApplicationJson = "application/json"

  def aTeamCityClient(baseUrl: String, timeout: Int, username: String, password: String): TeamCityClient = {
    val httpClient = new HttpClientWrapper(username, password, timeout)
    new TeamCityClient(httpClient, baseUrl)
  }
}

object MapperFactory {
  def createMapper(): ObjectMapper = {
    val objectMapper = new ObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper
  }
}
