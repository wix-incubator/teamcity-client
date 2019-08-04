package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client._

trait VcsRootContext extends PropertiesContext with ContextBase {

  val vcsRootId = "vcsRootId"
  val vcsRootName = "vcsRootName"
  val vcsRootUrl = "href"


  val baseVcsRoot = BaseVcsRoot(vcsRootId, vcsRootName, Some(vcsRootUrl))
  val vcsRoot = VcsRoot("vcsRootId", vcsRootName, "vcsName", "Href", Some("status"), Some("lastChecked"), baseProject, properties)
  val vcsRoots = VcsRoots(1, Some("hrf"), Some(List(baseVcsRoot)))
  val vcsRootEntry = VcsRootEntry("id", "checkourRules", baseVcsRoot)
  val vcsRootEntries = VcsRootEntries(1, Some(List(vcsRootEntry)))

  val getBuildTypesByRootId = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes?locator=vcsRoot:(id:$vcsRootId)"
  val vcsRootsUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/vcs-roots"
  val setVcsRootPropertiesUrl = s"$vcsRootsUrl/id:$vcsRootId/properties/${vcsRoot.properties.property.head.name}"
  val getVcsRootsByIdUrl = s"$vcsRootsUrl/id:$vcsRootId"
  val getVcsRootsByNameUrl = s"$vcsRootsUrl/name:$vcsRootName"
  val getVcsRootsByUrl = s"$vcsRootsUrl/?locator=property:(name:url,value:$vcsRootUrl)"

  httpClient.executeGet(vcsRootsUrl) returns writeObjectAsJson(vcsRoots)
  httpClient.executePost(vcsRootsUrl, writeObjectAsJson(vcsRoot)) returns writeObjectAsJson(baseVcsRoot)
  httpClient.executeGet(getVcsRootsByIdUrl) returns writeObjectAsJson(vcsRoot)
  httpClient.executeGet(getVcsRootsByNameUrl) returns writeObjectAsJson(vcsRoot)
  httpClient.executeGet(getVcsRootsByUrl) returns writeObjectAsJson(vcsRoot)
}
