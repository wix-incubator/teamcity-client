package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client._

trait ProjectContext extends ContextBase {

  val projects = Projects(1, List(baseProject))
  val project = Project(baseProject.id, baseProject.name, "parentProjId1", "some-href", "some-weburl", Projects(0, List()), baseProject, BuildTypes(0, List()), None)

  val projectUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/projects"
  val projectWithIdUrl = s"$projectUrl/id:${baseProject.id}"
  val projectWithNameUrl = s"$projectUrl/name:${baseProject.name}"
  val setProjectNameUrl = s"$projectWithIdUrl/name"
  val setProjectDescriptionUrl = s"$projectWithIdUrl/description"
  val setProjectArchivedUrl = s"$projectWithIdUrl/archived"

  httpClient.executePost(projectUrl, writeObjectAsJson(baseProject)) returns writeObjectAsJson(baseProject)
  httpClient.executeGet(projectUrl) returns writeObjectAsJson(projects)
  httpClient.executeGet(projectWithIdUrl) returns writeObjectAsJson(project)
  httpClient.executeGet(projectWithNameUrl) returns writeObjectAsJson(project)
}
