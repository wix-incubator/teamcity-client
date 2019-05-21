package com.wix.ci.teamcity.client

import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper
import org.specs2.matcher.{MustThrownExpectations, Scope}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

class TeamCityClientTest extends SpecificationWithJUnit with Mockito {
  "create project" should {
    "call execute post and pass base project in body" in new Context {
      teamcityClient.createProject(baseProject)
      there was one(httpClient).executePost(createProjectUrl, writeObjectAsJson(baseProject))
      there was one(httpClient).executePutPlainText(setProjectArchivedUrl, baseProject.archived.toString)
      there was one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get)
    }
  }

  "delete project" should {
    "call delete endpoint with project id" in new Context {
      teamcityClient.deleteProject(baseProject.id)
      there was one(httpClient).executeDelete(deleteProjectUrl)
    }
  }

  "get projects" should {
    "return a list of all base projects" in new Context {
      teamcityClient.getProjects must beEqualTo(projects)
    }
  }

  "get project by id" should {
    "return the project" in new Context {
      teamcityClient.getProjectById(projectId) must beEqualTo(project)
    }
  }

  "set project name" should {
    "call set project name endpoint" in new Context {
      teamcityClient.setProjectName(projectId, projectName)
      there was one(httpClient).executePutPlainText(setProjectNameUrl, projectName)
    }
  }

  "set project description" should {
    "call set project description endpoint" in new Context {
      teamcityClient.setProjectDescription(projectId, baseProject.description.get)
      there was one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get)
    }
  }

  "set project archived" should{
    "call the set project archived endpint" in new Context{
      teamcityClient.setProjectArchived(projectId,true)
      there was one(httpClient).executePutPlainText(setProjectArchivedUrl,true.toString)
    }
  }

  "get build types" should {
    "return a list of build types " in new Context {
      teamcityClient.getBuildTypes must beEqualTo(buildTypes)
    }
  }

  "get build types by vcs root id" should {
    "return a list of build types" in new Context {
      teamcityClient.getBuildTypesByVcsRootId(vcsRootId) must beEqualTo(buildTypes)
    }
  }

  "create build type" should {
    "return base build type" in new Context {
      teamcityClient.createBuildType(baseBuildTypes, projectId) must beEqualTo(baseBuildTypes)
    }
  }

  "get vcs roots" should {
    "return a list of vcs roots" in new Context {
      teamcityClient.getVcsRoots() must beEqualTo(Seq(baseVcsRoot))
    }
  }

  "get vcs root by id" should {
    "return vcs root" in new Context {
      teamcityClient.getVcsRootById(vcsRootId) must beEqualTo(vcsRoot)
    }
  }

//  "get vcs root by name" should {
//    "return vcs root" in new Context {
//      teamcityClient.getVcsRoots() must beEqualTo(Seq(baseVcsRoot))
//    }
//  }
//
//  "get vcs root by vcs url" should {
//    "return vcs root" in new Context {
//      teamcityClient.getVcsRoots() must beEqualTo(Seq(baseVcsRoot))
//    }
//  }
}


//  "xxx" should{
//    "xxxx" in{
//      val wrapper = new HttpClientWrapper("admin","admin")
//      val x = wrapper.executeGet("http://localhost:8111/httpAuth/app/rest/projects")
//      println(x)
//      ok
//    }
//  }

trait Context extends Scope with Mockito with MustThrownExpectations {

  val httpClient = mock[HttpClientWrapper]
  val baseUrl = "http://localhost:8888/my-teamcity"
  val teamcityClient = new TeamCityClient(httpClient, baseUrl)
  val projectId = "projid"
  val projectName = "projName"
  val vcsRootId = "vcsRootId"
  val vcsRootName = "vcsRootName"
  val property = Property("propName", "value", true)
  val properties = Properties(Seq(property))
  val baseProject = BaseProject(projectId, "projName","some-href","some-web-url",Some("desc"),false,Some("some-parent-proj"))
  val mapper = MapperFactory.createMapper()
  val buildTypes = BuildTypes(0, List())
  val baseTemplate = BaseTemplate("tempId", "tempName", "href", projectName, projectId)
  val baseBuildTypes = BaseBuildType("buildId", "buildName", "desc", baseTemplate, projectName, projectId)
  val projects = Projects(1, List(baseProject))
  val project = Project(projectId, projectName, "parentProjId1", "some-href", "some-weburl", Projects(0,List()), baseProject, BuildTypes(0,List()), None)
  val baseVcsRoot = BaseVcsRoot(vcsRootId, vcsRootName, "href")
  val vcsRoot = VcsRoot(vcsRootName, "status", "lastChecked", baseProject, properties)
  val vcsRoots = VcsRoots(1, Seq(baseVcsRoot))

  val createProjectUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
  val getProjectsUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
  val setProjectNameUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/name"
  val setProjectDescriptionUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/description"
  val setProjectArchivedUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/archived"
  val getProjectByIdUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}"
  val deleteProjectUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}"
  val createBuildTypeUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/buildTypes"
  val getBuildTypes = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes"
  val getBuildTypesByRootId = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes?locator=vcsRoot:(id:${vcsRootId})"
  val getVcsRootsUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots"
  val getVcsRootsByIdUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId"


  httpClient.executePost(createProjectUrl, writeObjectAsJson(baseProject)) returns writeObjectAsJson(baseProject)
  httpClient.executeGet(getProjectsUrl) returns writeObjectAsJson(projects)
  httpClient.executeGet(getProjectByIdUrl) returns writeObjectAsJson(project)
  httpClient.executeGet(getBuildTypes) returns writeObjectAsJson(buildTypes)
  httpClient.executeGet(getBuildTypesByRootId) returns writeObjectAsJson(buildTypes)
  httpClient.executePost(createBuildTypeUrl, writeObjectAsJson(baseBuildTypes)) returns writeObjectAsJson(baseBuildTypes)
  httpClient.executeGet(getVcsRootsUrl) returns writeObjectAsJson(vcsRoots)
  httpClient.executeGet(getVcsRootsByIdUrl) returns writeObjectAsJson(vcsRoot)

  def writeObjectAsJson(obj: AnyRef): String = {
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
  }
}


