package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{BaseBuild, Build, Builds, TeamCityClient}

trait BuildContext extends ContextBase {
  val baseBuild = BaseBuild("id", "typeId", None, None, None)
  val build = Build(baseBuild.buildTypeId, None, None)

  val buildQueueUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildQueue"
  val getBuildUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/builds/id:${ baseBuild.id }"
  val getRunningBuildUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/builds?locator=running:true"

  httpClient.executeGet(buildQueueUrl) returns writeObjectAsJson(Builds(1, List(baseBuild)))
  httpClient.executePost(buildQueueUrl, writeObjectAsJson(build)) returns writeObjectAsJson(build)
  httpClient.executeGet(getBuildUrl) returns writeObjectAsJson(build)
  httpClient.executeGet(getRunningBuildUrl) returns writeObjectAsJson(Builds(1, List(baseBuild)))
}
