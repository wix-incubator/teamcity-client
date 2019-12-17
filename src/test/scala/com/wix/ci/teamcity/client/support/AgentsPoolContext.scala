package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client._

trait AgentsPoolContext extends ContextBase {
  val agentPoolList = List(BaseAgentPool(1, "name", "href"))
  val agents = Agents(1, List.empty)
  val projectsList = List(
    BaseProject(
      "id",
      "name",
      Some("href"),
      Some("webUrl"),
      Some("description"),
      archived = false,
      Some("parent")))
  val project = Project(baseProject.id,
    baseProject.name,
    "parentProjId1",
    "some-href",
    "some-weburl",
    Projects(0, List()),
    baseProject,
    BuildTypes(0, List()),
    None)
  val agentPool = AgentPool(1, "name", "href", Projects(1, projectsList), agents)
  val agentPools = AgentPools(agentPoolList, 1, "href")

  val agentPoolsUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/agentPools"
  httpClient.executeGet(agentPoolsUrl) returns writeObjectAsJson(agentPools)
  httpClient.executePost(agentPoolsUrl, writeObjectAsJson(agentPoolList.head)) returns writeObjectAsJson(agentPool)
  httpClient.executeGet(s"$agentPoolsUrl/id:1") returns writeObjectAsJson(agentPool)
  httpClient.executePost(s"$agentPoolsUrl/id:1/projects", writeObjectAsJson(baseProject)) returns writeObjectAsJson(project)
  httpClient.executePost(s"$agentPoolsUrl/id:1/agents", """{"id":"1"}""") returns writeObjectAsJson(agentPool)

}
