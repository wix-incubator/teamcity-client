package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{Agents, BaseAgent, TeamCityClient}

trait AgentContext extends BuildTypesContext with ContextBase{
  val agentId = 11
  val agentName = "myAgent"
  val agentType = 123

  val getAgentsUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/agents?locator=authorized:any"
  val authorizeAgentUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/agents/id:$agentId/authorized"


  val baseAgent = BaseAgent(agentId,agentName,agentType)
  val agents = Agents(count=1,List(baseAgent))
  httpClient.executeGet(getAgentsUrl) returns writeObjectAsJson(agents)
}
