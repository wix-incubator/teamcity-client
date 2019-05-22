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

    "create a new project retrieve it and delete it" in new Context {
      val createdProj = teamcityClient.createProject(baseProject)
      createdProj must beEqualTo(baseProject.copy(description = None)) //project settings are not returned here
      teamcityClient.getProjects.project.contains(baseProject) must beTrue
      teamcityClient.getProjectByName(baseProject.name) must beEqualTo(project)
      teamcityClient.getProjectById(baseProject.id) must beEqualTo(project)
      teamcityClient.setProjectName(baseProject.id, newProjectName)
      teamcityClient.getProjectById(baseProject.id) must beEqualTo(project.copy(name = newProjectName))
      teamcityClient.deleteProject(baseProject.id)
      teamcityClient.getProjects.project.contains(baseProject) must beFalse
    }

    "create vcs root retrieve it and delete it" in new Context {
      val baseVcsRes = teamcityClient.createVcsRoot(vcsRoot)
      baseVcsRes must beEqualTo(baseVcsRoot)
      teamcityClient.getVcsRoots() must beEqualTo(vcsRoots)
      teamcityClient.getVcsRootById(vcsRoot.id) must beEqualTo(vcsRoot)
      teamcityClient.getVcsRootByName(vcsRoot.name) must beEqualTo(vcsRoot)
      teamcityClient.deleteVcsRoot(vcsRoot.id)
      teamcityClient.getVcsRoots() must beEqualTo(VcsRoots(0, "/httpAuth/app/rest/vcs-roots", None))
    }


    "create build types retrieve them and delete" in new Context {
      val createdProj = teamcityClient.createProject(baseProject)
      teamcityClient.createBuildType(baseBuildType) must beEqualTo(baseBuildType.copy(description = None))
      teamcityClient.createBuildType(baseBuildType2) must beEqualTo(baseBuildType2.copy(description = None))
      teamcityClient.getBuildTypes() must beEqualTo(buildTypes)
      teamcityClient.deleteBuildType(baseBuildType.id)
      teamcityClient.deleteBuildType(baseBuildType2.id)
      teamcityClient.getBuildTypes() must beEqualTo(BuildTypes(0,List()))
      teamcityClient.deleteProject(baseProject.id)

    }

    "set build type root entries" in new Context{
      teamcityClient.createProject(baseProject)
      teamcityClient.createBuildType(baseBuildType)
      teamcityClient.createVcsRoot(vcsRoot)
      teamcityClient.createBuildTypeVcsRootEntries(baseBuildType.id,vcsRootEntries)
      teamcityClient.setBuildTypeVcsRootEntry(baseBuildType.id,vcsRootEntries.vcsRootEntry.get.head) must beEqualTo(vcsRootEntries.vcsRootEntry.get.head)
    }

    "creates template retrieve it" in new Context{
      val res = teamcityClient.createTemplate(baseTemplate)
      res must beEqualTo(template)
      teamcityClient.getTemplates must beEqualTo(templates)
      teamcityClient.deleteTemplate(baseTemplate.id)
      teamcityClient.getTemplates must beEqualTo(Templates(0,Some(List())))
    }

    "get team city server details" in new Context{
      teamcityClient.getTeamCityServerDetails.copy(currentTime = "",startTime = "") must beEqualTo(teamCityServerDetails)
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
    val projectId = "projid"
    val projectName = "projName"
    val rootProjectId = "_Root"
    val rootProjectName = "<Root project>"
    val httpClient = new HttpClientWrapper(username, password)
    val teamcityClient = new TeamCityClient(httpClient, teamcityBaseUrl)
    val rootBaseProject = BaseProject(rootProjectId,rootProjectName, "/httpAuth/app/rest/projects/id:_Root", "http://localhost:8111/project.html?projectId=_Root", Some("Contains all other projects"), false, None)

    val property = Property("ignoreKnownHosts", "true")
    val baseProject = BaseProject(projectId, projectName, "/httpAuth/app/rest/projects/id:projid", "http://localhost:8111/project.html?projectId=projid", Some("projDesc"), false, Some("_Root"))
    val project = Project(baseProject.id, baseProject.name, baseProject.parentProjectId.get, baseProject.href, baseProject.webUrl, Projects(0, null), rootBaseProject, BuildTypes(0, List()), templates = Some(Templates(0, Option(List()))))
    val vcsRoot = VcsRoot("somevcsroot", "some vcs root", "jetbrains.git", "/httpAuth/app/rest/vcs-roots/id:somevcsroot", None, None, rootBaseProject, Properties(List(property)))
    val baseVcsRoot = BaseVcsRoot("somevcsroot", "some vcs root", "/httpAuth/app/rest/vcs-roots/id:somevcsroot")
    val vcsRoots = VcsRoots(1, "/httpAuth/app/rest/vcs-roots", Some(List(baseVcsRoot)))


    val baseBuildType = BaseBuildType("myBuildTypeId", "my build type", Some("some desc"), None, projectName, projectId, false)
    val baseBuildType2 = BaseBuildType("myBuildTypeId2", "my build type2", Some("some desc"), None, projectName, projectId, false)
    val buildTypes = BuildTypes(2, List(baseBuildType.copy(description = None), baseBuildType2.copy(description = None)))
    val vcsRootEntries = VcsRootEntries(1,Some(List(VcsRootEntry(baseVcsRoot.id,"some checkout rules",baseVcsRoot))))

    val baseTemplate = BaseTemplate("template1", "some template","/httpAuth/app/rest/buildTypes/id:template1",rootProjectId,rootProjectName)
    val template = Template("template1","some template","/httpAuth/app/rest/buildTypes/id:template1",rootProjectId,rootProjectName,rootBaseProject,false,true)
    val templates = Templates(1,Some(List(baseTemplate)))
    val teamCityServerDetails = new TeamCityServerDetails("58744","20181218T000000+0000","2018.1.5 (build 58744)",2018,1,"","")

  }

}