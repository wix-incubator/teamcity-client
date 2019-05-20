package com.wix.ci.teamcity.client

import com.wix.ci.teamcity.client.scalajhttp.HttpClientWrapper
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.matcher.{MustThrownExpectations, Scope}
import org.specs2.mock.Mockito

class TeamCityClientTest  extends SpecificationWithJUnit with Mockito{
  "create project" should{
    "call execute post and pass base project in body" in new Context{
      teamcityClient.createProject(baseProject)
      there was one(httpClient).executePost(createProjectUrl,writeObjectAsJson(baseProject))
    }
  }

  "delete project" should{
    "call delete endpint with project id" in new Context{
      teamcityClient.deleteProject(baseProject.id)
      there was one(httpClient).executeDelete(deleteProjectUrl)
    }
  }

  "get projects" should{
    "return a list of all base projects" in new Context{
      teamcityClient.getProjects must beEqualTo(projects)
    }
  }

  "get project by id" should{
    "return the project " in new  Context{
      teamcityClient.getProjectById(projectId) must beEqualTo(project)
    }
  }












//  "xxx" should{
//    "xxxx" in{
//      val wrapper = new HttpClientWrapper("admin","admin")
//      val x = wrapper.executeGet("http://localhost:8111/httpAuth/app/rest/projects")
//      println(x)
//      ok
//    }
//  }

  trait Context extends Scope with Mockito with MustThrownExpectations{

    val httpClient = mock[HttpClientWrapper]
    val baseUrl = "http://localhost:8888/my-teamcity"
    val teamcityClient = new TeamCityClient(httpClient,baseUrl)
    val projectId = "projid"
    val projectName = "projName"
    val baseProject = BaseProject(projectId, "projName","some-href","some-web-url",Some("desc"),false,Some("some-parent-proj"))
    val mapper = MapperFactory.createMapper()
    val projects = Projects(1,List(baseProject))
    val project = Project(projectId,projectName,"parentProjId1","some-href","some-weburl",Projects(0,List()),baseProject,BuildTypes(0,List()),None)

    val createProjectUrl  = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
    val getProjectsUrl    = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
    val getProjectByIdUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}"
    val deleteProjectUrl  = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}"


    httpClient.executePost(createProjectUrl,writeObjectAsJson(baseProject) )returns writeObjectAsJson(baseProject)
    httpClient.executeGet(getProjectsUrl) returns writeObjectAsJson(projects)
    httpClient.executeGet(getProjectByIdUrl) returns writeObjectAsJson(project)

    def writeObjectAsJson(obj : AnyRef) : String = {
      mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
    }
  }
}


