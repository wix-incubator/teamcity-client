package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{BaseBuild, Build, TeamCityClient}

trait BuildContext extends ContextBase {
  val getBuildQueueUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildQueue"
  val addBuildToQueueUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildQueue"

  val baseBuild = BaseBuild("id", "typeId", "number", "status", "state")
  //val build = Build(baseBuild.id, baseBuild.buildTypeId, "history", baseBuild.number, baseBuild.status, baseBuild.state, )

  httpClient.executeGet(getBuildQueueUrl) returns writeObjectAsJson(List(baseBuild))
 // httpClient.executePost(getBuildQueueUrl) returns writeObjectAsJson(List(baseBuild))
}
