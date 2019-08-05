package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{Step, Steps, TeamCityClient}


trait StepContext extends BuildTypesContext with ContextBase {
  val buildStepUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes/id:${baseBuildTypes.id}/steps"

  val step = Step("stepId", "stepName", "type", properties)
  val steps = Steps(1, Some(List(step)))

  httpClient.executePost(buildStepUrl, writeObjectAsJson(step)) returns writeObjectAsJson(step)
  httpClient.executeGet(buildStepUrl) returns writeObjectAsJson(steps)
}
