package com.wix.ci.teamcity.client.it

import com.wix.ci.teamcity.client._
import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper
import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.BeforeAfterAll

import scala.util.Try

class TeamCityClientIT extends SpecificationWithJUnit with BeforeAfterAll with ITEnv {
  sequential

  "teamcity client" should {
    "get projects containing only top level project" in new Context {
      teamcityClient.getProjects must beEqualTo(Projects(1, List(rootBaseProject)))
    }

    "create a new project get it and delete it" in new Context {
      val createdProj = teamcityClient.createProject(baseProject)
      createdProj must beEqualTo(baseProject.copy(description = None)) //project settings are not returned here
      teamcityClient.getProjects.project.contains(baseProject) must beTrue
      teamcityClient.getProjectById(baseProject.id) must beEqualTo(project)
      teamcityClient.setProjectName(baseProject.id, newProjectName)
      teamcityClient.getProjectById(baseProject.id) must beEqualTo(project.copy(name = newProjectName))
      teamcityClient.deleteProject(baseProject.id)
      teamcityClient.getProjects.project.contains(baseProject) must beFalse
    }

    "create vcs root " in new Context {
      val res = teamcityClient.createVcsRoot(vcsRoot)
      res must beEqualTo(baseVcsRoot)
      teamcityClient.getVcsRoots() must beEqualTo(vcsRoots)
    }
  }


  override def beforeAll(): Unit = {
    Try(killTeamcityDocker())
    loadTeamcityDockerImage()
    startTeamcityDocker()
  }

  override def afterAll(): Unit = killTeamcityDocker()


  trait Context extends Scope {
    val newProjectName = "proj2"
    val teamcityBaseUrl = "http://localhost:8111"
    val username = "admin"
    val password = "admin"
    val httpClient = new HttpClientWrapper(username, password)
    val teamcityClient = new TeamCityClient(httpClient, teamcityBaseUrl)
    val rootBaseProject = BaseProject("_Root", "<Root project>", "/httpAuth/app/rest/projects/id:_Root", "http://localhost:8111/project.html?projectId=_Root", Some("Contains all other projects"), false, None)

    val baseProject = BaseProject("projid", "projName", "/httpAuth/app/rest/projects/id:projid", "http://localhost:8111/project.html?projectId=projid", Some("projDesc"), false, Some("_Root"))
    val project = Project(baseProject.id, baseProject.name, baseProject.parentProjectId.get, baseProject.href, baseProject.webUrl, Projects(0, null), rootBaseProject, BuildTypes(0, List()))
    val vcsRoot = VcsRoot("somevcsroot", "some vcs root", "jetbrains.git", "/httpAuth/app/rest/vcs-roots/id:somevcsroot", None, None, rootBaseProject, new Properties(Seq()))
    val baseVcsRoot = BaseVcsRoot("somevcsroot", "some vcs root", "/httpAuth/app/rest/vcs-roots/id:somevcsroot")
    val vcsRoots = VcsRoots(1, "/httpAuth/app/rest/vcs-roots", List(baseVcsRoot))
  }

}
