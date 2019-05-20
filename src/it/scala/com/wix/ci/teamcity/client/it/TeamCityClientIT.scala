package com.wix.ci.teamcity.client.it

import com.wix.ci.teamcity.client.{BaseProject, TeamCityClient}
import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.BeforeAfterAll
import org.specs2.matcher.Scope

import scala.util.Try

class TeamCityClientIT extends SpecificationWithJUnit with BeforeAfterAll with ITEnv {
  "teamcity client" should{
    "create project " in new Context{
      teamcityClient.getProjects must beEqualTo(baseProject)
    }
  }









  override def beforeAll(): Unit = {
    Try(killTeamcityDocker())
    loadTeamcityDockerImage
    startTeamcityDocker()
  }

  override def afterAll(): Unit = killTeamcityDocker()


  trait Context extends Scope{
    val teamcityBaseUrl = "http://localhost:8111"
    val username = "admin"
    val password = "admin"
    val httpClient = new HttpClientWrapper(username, password)
    val teamcityClient = new TeamCityClient(httpClient,teamcityBaseUrl)

    val baseProject = BaseProject("projid", "projName","some-href","some-web-url","desc",false,Some("some-parent-proj"))


  }
}
