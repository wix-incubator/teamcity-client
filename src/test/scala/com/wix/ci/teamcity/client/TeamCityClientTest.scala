package com.wix.ci.teamcity.client

import com.wix.ci.teamcity.client.support._
import org.specs2.mutable.SpecificationWithJUnit

class TeamCityClientTest extends SpecificationWithJUnit {

  "create project" should {
    "call execute post and pass base project in body" in new ProjectContext {
      teamcityClient.createProject(baseProject)
      there was one(httpClient).executePost(projectUrl, writeObjectAsJson(baseProject))
      there was one(httpClient).executePutPlainText(setProjectArchivedUrl, baseProject.archived.toString)
      there was one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get)
    }
  }

  "set project description" should {
    "call set project description endpoint" in new ProjectContext {
      teamcityClient.setProjectDescription(baseProject.id, baseProject.description.get)
      there was one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get)
    }
  }

  "set project archived" should {
    "call the set project archived endpint" in new ProjectContext {
      teamcityClient.setProjectArchived(baseProject.id, archived = true)
      there was one(httpClient).executePutPlainText(setProjectArchivedUrl, true.toString)
    }
  }

  "set project name" should {
    "call set project name endpoint" in new ProjectContext {
      teamcityClient.setProjectName(baseProject.id, baseProject.name)
      there was one(httpClient).executePutPlainText(setProjectNameUrl, baseProject.name)
    }
  }

  "get projects" should {
    "return a list of all base projects" in new ProjectContext {
      teamcityClient.getProjects must beEqualTo(projects)
    }
  }

  "delete project" should {
    "call delete endpoint with project id" in new ProjectContext {
      teamcityClient.deleteProject(baseProject.id)
      there was one(httpClient).executeDelete(projectWithIdUrl)
    }
  }

  "get project by name" should {
    "return the project" in new ProjectContext {
      teamcityClient.getProjectByName(baseProject.name) must beEqualTo(project)
    }
  }

  "get project by id" should {
    "return the project" in new ProjectContext {
      teamcityClient.getProjectById(baseProject.id) must beEqualTo(project)
    }
  }


  "get build types" should {
    "return a list of build types " in new BuildTypesContext {
      teamcityClient.getBuildTypes must beEqualTo(buildTypes)
    }
  }

  "get build types by vcs root id" should {
    "return a list of build types" in new BuildTypesContext {
      teamcityClient.getBuildTypesByVcsRootId(vcsRootId) must beEqualTo(buildTypes)
    }
  }

  "create build type" should {
    "return base build type" in new BuildTypesContext {
      teamcityClient.createBuildType(baseBuildTypes) must beEqualTo(baseBuildTypes)
    }
  }

  "delete build type" should {
    "delete build type" in new BuildTypesContext {
      teamcityClient.deleteBuildType(baseBuildTypes.id)
      there was one(httpClient).executeDelete(deleteBuildTypeUrl)
    }
  }

  "create vcs roots" should {
    "return a list of vcs roots" in new VcsRootContext {
      teamcityClient.createVcsRoot(vcsRoot) must beEqualTo(baseVcsRoot)
      there was one(httpClient).executePutPlainText(setVcsRootPropertiesUrl, vcsRoot.properties.property.head.value)
    }
  }

  "get vcs roots" should {
    "return a list of vcs roots" in new VcsRootContext {
      teamcityClient.getVcsRoots() must beEqualTo(VcsRoots(1, Some("hrf"), Some(List(baseVcsRoot))))
    }
  }

  "get vcs root by id" should {
    "return vcs root" in new VcsRootContext {
      teamcityClient.getVcsRootById(vcsRootId) must beEqualTo(vcsRoot)
    }
  }

  "get vcs root by name" should {
    "return vcs root" in new VcsRootContext {
      teamcityClient.getVcsRootByName(vcsRootName) must beEqualTo(vcsRoot)
    }
  }

  "get vcs root by url" should {
    "return vcs root" in new VcsRootContext {
      teamcityClient.getVcsRootByUrl(vcsRootUrl) must beEqualTo(vcsRoot)
    }
  }

  "delete vcs root" should {
    "delete vcs root" in new VcsRootContext {
      teamcityClient.deleteVcsRoot(vcsRootId)
      there was one(httpClient).executeDelete(getVcsRootsByIdUrl)
    }
  }


  "create build type vcs root entries" should {
    "return vcs root" in new BuildTypesContext {
      teamcityClient.createBuildTypeVcsRootEntries(baseBuildTypes.id, vcsRootEntries)
      there was one(httpClient).executePost(createBuildTypeVcsRootEntries, writeObjectAsJson(vcsRootEntry))
    }
  }

  "set build type vcs root entry" should {
    "return vcs root" in new BuildTypesContext {
      teamcityClient.setBuildTypeVcsRootEntry(baseBuildTypes.id, vcsRootEntry) must beEqualTo(vcsRootEntry)
    }
  }

  "get teamCity server details" should {
    "return teamCity server details" in new ServerDetailsContext {
      teamcityClient.getTeamCityServerDetails() must beEqualTo(teamCityServerDetails)
    }
  }

  "create template" should {
    "return template" in new TemplateContext {
      teamcityClient.createTemplate(baseTemplate) must beEqualTo(template)
    }
  }

  "get templates" should {
    "return a list of templates" in new TemplateContext {
      teamcityClient.getTemplates() must beEqualTo(templates)
    }
  }

  "delete template" should {
    "delete template" in new TemplateContext {
      teamcityClient.deleteTemplate(baseTemplate.id)
      there was one(httpClient).executeDelete(templateUrl)
    }
  }

  "create snapshot dependency" should {
    "create snapshot dependency" in new SnapshotDependenciesContext {
      teamcityClient.createSnapshotDependency(baseBuildTypes.id, snapshotDependency) must beEqualTo(snapshotDependency)
    }
  }

  "get snapshot dependency" should {
    "get snapshot dependency" in new SnapshotDependenciesContext {
      teamcityClient.getSnapShotDependencies(baseBuildTypes.id) must beEqualTo(snapshotDependencies)
    }
  }

  "delete snapshot dependency" should {
    "delete snapshot dependency" in new SnapshotDependenciesContext {
      teamcityClient.deleteSnapshotDependency(baseBuildTypes.id, snapshotDependency.id)
      val url = s"$snapshotDependenciesUrl/${snapshotDependency.id}"
      there was one(httpClient).executeDelete(url)
    }

    "create build step" should {
      "create build step" in new StepContext {
        teamcityClient.createBuildStep(baseBuildTypes.id, step) must beEqualTo(step)
      }
    }

    "get build step" should {
      "get build step" in new StepContext {
        teamcityClient.getBuildSteps(baseBuildTypes.id) must beEqualTo(steps)
      }
    }

    "delete build step" should {
      "delete build step" in new StepContext {
        teamcityClient.deleteBuildStep(baseBuildTypes.id, step.id)
        val url = s"$buildStepUrl/${step.id}"
        there was one(httpClient).executeDelete(url)
      }
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