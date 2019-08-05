package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper
import com.wix.ci.teamcity.client.{BaseProject, MapperFactory, TeamCityClient}
import org.specs2.matcher.{MustThrownExpectations, Scope}
import org.specs2.mock.Mockito

trait ContextBase extends Scope with Mockito with MustThrownExpectations {

  val httpClient = mock[HttpClientWrapper]
  val mapper = MapperFactory.createMapper()
  val baseUrl = "http://localhost:8888/my-teamcity"
  val teamcityClient = new TeamCityClient(httpClient, baseUrl)

  private val projectId = "projid"
  private val projName = "projName"
  private val href = "some-href"
  protected val baseProject = BaseProject(projectId, projName, Some(href), Some("some-web-url"), Some("desc"), archived = false, Some("some-parent-proj"))
  val acceptTextPlain = "text/plain"

  protected def writeObjectAsJson(obj: AnyRef): String = {
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
  }

}
