package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{Properties, TeamCityClient, Trigger}

trait TriggerContext extends BuildTypesContext with ContextBase {
  val triggerName = "trigger1"
  val triggerType = "VCS Trigger"
  val triggerProps = Properties(List())
  val _trigger = Trigger(triggerName,triggerType,triggerProps)
  val addTriggerUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes/id:$buildTypeId/triggers"


  httpClient.executePost(addTriggerUrl,writeObjectAsJson(_trigger)) returns writeObjectAsJson(_trigger)
}
