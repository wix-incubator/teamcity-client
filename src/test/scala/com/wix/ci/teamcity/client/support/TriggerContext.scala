package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{Properties, TeamCityClient, Trigger}

trait TriggerContext extends BuildTypesContext with ContextBase {
  val triggerId = "trigger1"
  val triggerType = "VCS Trigger"
  val triggerProps = Properties(List())
  val _trigger = Trigger(triggerId, triggerType, triggerProps)
  val addTriggerUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/triggers"
  val deleteTriggerUrl = s"$baseUrl/${ TeamCityClient.contextPrefix }/buildTypes/id:$buildTypeId/triggers/$triggerId"


  httpClient.executePost(addTriggerUrl, writeObjectAsJson(_trigger)) returns writeObjectAsJson(_trigger)
}
