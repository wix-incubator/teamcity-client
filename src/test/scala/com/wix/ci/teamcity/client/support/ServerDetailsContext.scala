package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{TeamCityClient, TeamCityServerDetails}

trait ServerDetailsContext extends ContextBase {

  val teamCityServerDetails = TeamCityServerDetails("buildNumber", "date", "version", 1, 2, "currentTime", "startTime")
  val getTeamCityServerDetails = s"$baseUrl/${TeamCityClient.contextPrefix}/server"

  httpClient.executeGet(getTeamCityServerDetails) returns writeObjectAsJson(teamCityServerDetails)
}