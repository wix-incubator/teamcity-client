package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{BaseBuildType, BaseTemplate, BuildType, BuildTypes, Properties, Property, TeamCityClient, Trigger, Triggers}

trait BuildTypesContext extends TemplateContext with VcsRootContext with ContextBase {

  val buildTypes = BuildTypes(0, List())
  val baseBuildTypes = BaseBuildType("buildId", "buildName", Some("desc"), Some(baseTemplate), baseProject.name, baseProject.id, paused = false)

  val buildTypesUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes"
  val createBuildTypeUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/buildTypes"
  val deleteBuildTypeUrl = s"$buildTypesUrl/id:${baseBuildTypes.id}"
  val createBuildTypeVcsRootEntries = s"$buildTypesUrl/id:${baseBuildTypes.id}/vcs-root-entries"
  val getBuildType = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes/id:${baseBuildTypes.id}"

  val trigger = Trigger("id","name",Properties(List(Property("key","val"))))
  val triggers = Triggers(1,Option(List(trigger)))
  val buildType = BuildType(baseBuildTypes.id,baseBuildTypes.name,false,Some("desc"),baseProject.name,baseProject.id,
  Some("href"),Some("weburl"),Some(baseProject),Some(baseTemplate),None,Some(triggers),None,Some(vcsRootEntries),None,None,None,false )

  httpClient.executeGet(buildTypesUrl) returns writeObjectAsJson(buildTypes)
  httpClient.executeGet(getBuildTypesByRootId) returns writeObjectAsJson(buildTypes)
  httpClient.executePost(createBuildTypeUrl, writeObjectAsJson(baseBuildTypes)) returns writeObjectAsJson(baseBuildTypes)
  httpClient.executePost(createBuildTypeVcsRootEntries, writeObjectAsJson(vcsRootEntry)) returns writeObjectAsJson(vcsRootEntry)
  httpClient.executeGet(getBuildType)returns writeObjectAsJson(buildType)
}