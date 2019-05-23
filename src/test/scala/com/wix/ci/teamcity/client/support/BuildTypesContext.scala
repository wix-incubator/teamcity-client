package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{BaseBuildType, BuildTypes, TeamCityClient}

trait BuildTypesContext extends TemplateContext with VcsRootContext with ContextBase {

  val buildTypes = BuildTypes(0, List())
  val baseBuildTypes = BaseBuildType("buildId", "buildName", Some("desc"), Some(baseTemplate), baseProject.name, baseProject.id, paused = false)

  val buildTypesUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes"
  val createBuildTypeUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/buildTypes"
  val deleteBuildTypeUrl = s"$buildTypesUrl/id:${baseBuildTypes.id}"
  val createBuildTypeVcsRootEntries = s"$buildTypesUrl/id:${baseBuildTypes.id}/vcs-root-entries"

  httpClient.executeGet(buildTypesUrl) returns writeObjectAsJson(buildTypes)
  httpClient.executeGet(getBuildTypesByRootId) returns writeObjectAsJson(buildTypes)
  httpClient.executePost(createBuildTypeUrl, writeObjectAsJson(baseBuildTypes)) returns writeObjectAsJson(baseBuildTypes)
  httpClient.executePost(createBuildTypeVcsRootEntries, writeObjectAsJson(vcsRootEntry)) returns writeObjectAsJson(vcsRootEntry)
}