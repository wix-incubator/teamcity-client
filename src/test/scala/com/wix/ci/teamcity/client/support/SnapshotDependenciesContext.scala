package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{SnapshotDependencies, SnapshotDependency, TeamCityClient}

trait SnapshotDependenciesContext extends BuildTypesContext with ContextBase {

  val snapshotDependency = SnapshotDependency("depId", "type", properties, baseBuildTypes)
  val snapshotDependencies = SnapshotDependencies(1, Some(List(snapshotDependency)))
  val snapshotDependenciesUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes/id:${baseBuildTypes.id}/snapshot-dependencies"

  httpClient.executePost(snapshotDependenciesUrl, writeObjectAsJson(snapshotDependency)) returns writeObjectAsJson(snapshotDependency)
  httpClient.executeGet(snapshotDependenciesUrl) returns writeObjectAsJson(snapshotDependencies)
}
