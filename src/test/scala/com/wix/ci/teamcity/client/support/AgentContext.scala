package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{Agent, Agents, BaseAgent, TeamCityClient}

trait AgentContext extends BuildTypesContext with ContextBase {
  val agentId = 11
  val agentName = "myAgent"
  val agentType = 123

  val getAgentsUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents?locator=authorized:any"
  val authorizeAgentUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents/id:$agentId/authorized"
  val getAuthorizeAgentUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents?locator=authorized:true"
  val getAgentByIdUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents/id:$agentId"

  val baseAgent = BaseAgent(agentId, agentName, agentType)
  val agents = Agents(count = 1, List(baseAgent))
  val agent = Agent(agentId, agentName, agentType, "", uptodate = false, enabled = false, connected = false, authorized = false, null)
  httpClient.executeGet(getAgentsUrl) returns writeObjectAsJson(agents)
  httpClient.executeGet(getAuthorizeAgentUrl) returns writeObjectAsJson(agents)
  httpClient.executeGet(getAgentByIdUrl) returns writeObjectAsJson(agent)
}
