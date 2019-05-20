package com.wix.ci.teamcity.client.it

import com.wix.ci.teamcity.client.{BaseProject, Projects, TeamCityClient}
import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.BeforeAfterAll
import org.specs2.matcher.Scope

import scala.util.Try

class TeamCityClientIT extends SpecificationWithJUnit with BeforeAfterAll with ITEnv {
  "teamcity client" should{
    "get projects containing only top level project" in new Context{
      teamcityClient.getProjects must beEqualTo(Projects(1,List(rootBaseProject)))
    }

    "create a new project " in new Context{
      val createdProj = teamcityClient.createProject(baseProject)
      createdProj must beEqualTo(baseProject)
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
    val rootBaseProject = BaseProject("_Root","<Root project>","/httpAuth/app/rest/projects/id:_Root","http://localhost:8111/project.html?projectId=_Root",Some("Contains all other projects"),false,None)

    val baseProject = BaseProject("projid", "projName","/httpAuth/app/rest/projects/id:projid","http://localhost:8111/project.html?projectId=projid",None,false,Some("_Root"))


  }
}
