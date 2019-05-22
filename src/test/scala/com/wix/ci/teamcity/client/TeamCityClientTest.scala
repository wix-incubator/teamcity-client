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

  "set project description" should {
    "call set project description endpoint" in new Context {
      teamcityClient.setProjectDescription(projectId, baseProject.description.get)
      there was one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get)
    }
  }

  "set project archived" should {
    "call the set project archived endpint" in new Context {
      teamcityClient.setProjectArchived(projectId, true)
      there was one(httpClient).executePutPlainText(setProjectArchivedUrl, true.toString)
    }
  }

  "set project name" should {
    "call set project name endpoint" in new Context {
      teamcityClient.setProjectName(projectId, projectName)
      there was one(httpClient).executePutPlainText(setProjectNameUrl, projectName)
    }
  }

  "get projects" should {
    "return a list of all base projects" in new Context {
      teamcityClient.getProjects must beEqualTo(projects)
    }
  }

  "delete project" should {
    "call delete endpoint with project id" in new Context {
      teamcityClient.deleteProject(baseProject.id)
      there was one(httpClient).executeDelete(deleteProjectUrl)
    }
  }

  "get project by name" should {
    "return the project" in new Context {
      teamcityClient.getProjectByName(projectName) must beEqualTo(project)
    }
  }

  "get project by id" should {
    "return the project" in new Context {
      teamcityClient.getProjectById(projectId) must beEqualTo(project)
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
      teamcityClient.createBuildType(baseBuildTypes) must beEqualTo(baseBuildTypes)
    }
  }

  "delete build type" should {
    "delete build type" in new Context {
      teamcityClient.deleteBuildType(baseBuildTypes.id)
      there was one(httpClient).executeDelete(deleteBuildTypeUrl)
    }
  }

  "create vcs roots" should {
    "return a list of vcs roots" in new Context {
      teamcityClient.createVcsRoot(vcsRoot) must beEqualTo(baseVcsRoot)
      there was one(httpClient).executePutPlainText(setVcsRootPropertiesUrl, vcsRoot.properties.property.head.value)
    }
  }

  "get vcs roots" should {
    "return a list of vcs roots" in new Context {
      teamcityClient.getVcsRoots() must beEqualTo(VcsRoots(1, "hrf", Some(List(baseVcsRoot))))
    }
  }

  "get vcs root by id" should {
    "return vcs root" in new Context {
      teamcityClient.getVcsRootById(vcsRootId) must beEqualTo(vcsRoot)
    }
  }

  "get vcs root by name" should {
    "return vcs root" in new Context {
      teamcityClient.getVcsRootByName(vcsRootName) must beEqualTo(vcsRoot)
    }
  }

  "get vcs root by url" should {
    "return vcs root" in new Context {
      teamcityClient.getVcsRootByUrl(vcsRootUrl) must beEqualTo(vcsRoot)
    }
  }

  "delete vcs root" should {
    "delete vcs root" in new Context {
      teamcityClient.deleteVcsRoot(vcsRootId)
      there was one(httpClient).executeDelete(getVcsRootsByIdUrl)
    }
  }


  "create build type vcs root entries" should {
    "return vcs root" in new Context {
      teamcityClient.createBuildTypeVcsRootEntries(baseBuildTypes.id, vcsRootEntries)
      there was one(httpClient).executePost(createBuildTypeVcsRootEntries, writeObjectAsJson(vcsRootEntry))
    }
  }

  "set build type vcs root entry" should {
    "return vcs root" in new Context {
      teamcityClient.setBuildTypeVcsRootEntry(baseBuildTypes.id, vcsRootEntry) must beEqualTo(vcsRootEntry)
    }
  }

  "get teamCity server details" should {
    "return teamCity server details" in new Context {
      teamcityClient.getTeamCityServerDetails() must beEqualTo(teamCityServerDetails)
    }
  }

  "create template" should {
    "return template" in new Context {
      teamcityClient.createTemplate(baseTemplate) must beEqualTo(template)
    }
  }

  "get templates" should {
    "return a list of templates" in new Context {
      teamcityClient.getTemplates() must beEqualTo(templates)
    }
  }

  "delete template" should {
    "delete template" in new Context {
      teamcityClient.deleteTemplate(baseTemplate.id)
      there was one(httpClient).executeDelete(deleteTemplateUrl)
    }
  }

  //  "xxx" should{
  //    "xxxx" in{
  //      val wrapper = new HttpClientWrapper("admin","admin")
  //      val x = wrapper.executeGet("http://jvm-tc.dev.wixpress.com/httpAuth/app/rest/vcs-roots/id:adi_AdiAdiVcsRoot")
  //      println(x)
  //      ok
  //    }
  //  }
}


trait Context extends Scope with Mockito with MustThrownExpectations {

  val httpClient = mock[HttpClientWrapper]
  val baseUrl = "http://localhost:8888/my-teamcity"
  val teamcityClient = new TeamCityClient(httpClient, baseUrl)
  val projectId = "projid"
  val projectName = "projName"
  val vcsRootId = "vcsRootId"
  val vcsRootName = "vcsRootName"
  val vcsRootUrl = "href"
  val property = Property("propName", "value")
  val properties = Properties(List(property))
  val baseProject = BaseProject(projectId, "projName", "some-href", "some-web-url", Some("desc"), false, Some("some-parent-proj"))
  val mapper = MapperFactory.createMapper()
  val buildTypes = BuildTypes(0, List())
  val baseTemplate = BaseTemplate("tempId", "tempName", "href", projectName, projectId)
  val template = Template("tempId", "tempName", "href", projectId, projectName, baseProject, inherited = true)
  val templates = Templates(1, Some(List(baseTemplate)))
  val baseBuildTypes = BaseBuildType("buildId", "buildName", Some("desc"), Some(baseTemplate), projectName, projectId, false)
  val projects = Projects(1, List(baseProject))
  val project = Project(projectId, projectName, "parentProjId1", "some-href", "some-weburl", Projects(0, List()), baseProject, BuildTypes(0, List()), None)
  val baseVcsRoot = BaseVcsRoot(vcsRootId, vcsRootName, vcsRootUrl)
  val vcsRoot = VcsRoot("vcsRootId", vcsRootName, "vcsName", "Href", Some("status"), Some("lastChecked"), baseProject, properties)
  val vcsRoots = VcsRoots(1, "hrf", Some(List(baseVcsRoot)))
  val vcsRootEntry = VcsRootEntry("id", "checkourRules", baseVcsRoot)
  val vcsRootEntries = VcsRootEntries(1, Some(List(vcsRootEntry)))
  val teamCityServerDetails = TeamCityServerDetails("buildNumber", "date", "version", 1, 2, "currentTime", "startTime")

  val createProjectUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
  val getProjectsUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects"
  val setProjectNameUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/name"
  val setProjectDescriptionUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/description"
  val setProjectArchivedUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/archived"
  val getProjectByIdUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}"
  val getProjectByNameUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/name:${baseProject.name}"
  val deleteProjectUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}"
  val createBuildTypeUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseProject.id}/buildTypes"
  val getBuildTypesUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes"
  val deleteBuildTypeUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes/id:${baseBuildTypes.id}"
  val deleteTemplateUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes/id:${baseTemplate.id}"
  val createBuildTypeVcsRootEntries = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes/id:${baseBuildTypes.id}/vcs-root-entries"
  val getBuildTypesByRootId = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes?locator=vcsRoot:(id:${vcsRootId})"
  val getVcsRootsUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots"
  val setVcsRootPropertiesUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId/properties/${vcsRoot.properties.property.head.name}"
  val getVcsRootsByIdUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots/id:$vcsRootId"
  val getVcsRootsByNameUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots/name:$vcsRootName"
  val getVcsRootsByUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/vcs-roots/?locator=property:(name:url,value:$vcsRootUrl)"
  val getTeamCityServerDetails = s"${baseUrl}/${TeamCityClient.contextPrefix}/server"
  val postTemplateUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/projects/id:${baseTemplate.projectId}/templates"
  val getTemplatesUrl = s"${baseUrl}/${TeamCityClient.contextPrefix}/buildTypes?locator=templateFlag:true"


  httpClient.executePost(createProjectUrl, writeObjectAsJson(baseProject)) returns writeObjectAsJson(baseProject)
  httpClient.executeGet(getProjectsUrl) returns writeObjectAsJson(projects)
  httpClient.executeGet(getProjectByIdUrl) returns writeObjectAsJson(project)
  httpClient.executeGet(getProjectByNameUrl) returns writeObjectAsJson(project)
  httpClient.executeGet(getBuildTypesUrl) returns writeObjectAsJson(buildTypes)
  httpClient.executeGet(getBuildTypesByRootId) returns writeObjectAsJson(buildTypes)
  httpClient.executePost(createBuildTypeUrl, writeObjectAsJson(baseBuildTypes)) returns writeObjectAsJson(baseBuildTypes)
  httpClient.executeGet(getVcsRootsUrl) returns writeObjectAsJson(vcsRoots)
  httpClient.executePost(getVcsRootsUrl, writeObjectAsJson(vcsRoot)) returns writeObjectAsJson(baseVcsRoot)
  httpClient.executeGet(getVcsRootsByIdUrl) returns writeObjectAsJson(vcsRoot)
  httpClient.executeGet(getVcsRootsByNameUrl) returns writeObjectAsJson(vcsRoot)
  httpClient.executeGet(getVcsRootsByUrl) returns writeObjectAsJson(vcsRoot)
  httpClient.executePost(createBuildTypeVcsRootEntries, writeObjectAsJson(vcsRootEntry)) returns writeObjectAsJson(vcsRootEntry)
  httpClient.executeGet(getTeamCityServerDetails) returns writeObjectAsJson(teamCityServerDetails)
  httpClient.executePost(postTemplateUrl, writeObjectAsJson(baseTemplate)) returns writeObjectAsJson(template)
  httpClient.executeGet(getTemplatesUrl) returns writeObjectAsJson(templates)

  def writeObjectAsJson(obj: AnyRef): String = {
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
  }
}
