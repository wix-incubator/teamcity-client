package com.wix.ci.teamcity.client

import com.wix.ci.teamcity.client.support._
import org.specs2.mutable.SpecificationWithJUnit

class TeamCityClientTest extends SpecificationWithJUnit {

  "create project" should {
    "call execute post and pass base project in body" in new ProjectContext {
      teamcityClient.createProject(baseProject)
      there was one(httpClient).executePost(projectUrl, writeObjectAsJson(baseProject))
      there was
        one(httpClient).executePutPlainText(setProjectArchivedUrl, baseProject.archived.toString, acceptTextPlain)
      there was
        one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get, acceptTextPlain)
    }
  }

  "move project" in new ProjectContext {
    teamcityClient.moveProject(baseProject.id, parentBaseProject)
    there was one(httpClient).executePut(moveProjectUrl, writeObjectAsJson(parentBaseProject))
  }

  "set project description" should {
    "call set project description endpoint" in new ProjectContext {
      teamcityClient.setProjectDescription(baseProject.id, baseProject.description.get)
      there was
        one(httpClient).executePutPlainText(setProjectDescriptionUrl, baseProject.description.get, acceptTextPlain)
    }
  }

  "set project archived" should {
    "call the set project archived endpint" in new ProjectContext {
      teamcityClient.setProjectArchived(baseProject.id, archived = true)
      there was one(httpClient).executePutPlainText(setProjectArchivedUrl, true.toString, acceptTextPlain)
    }
  }

  "set project name" should {
    "call set project name endpoint" in new ProjectContext {
      teamcityClient.setProjectName(baseProject.id, baseProject.name)
      there was one(httpClient).executePutPlainText(setProjectNameUrl, baseProject.name, acceptTextPlain)
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

  "get build type" should {
    "return a the build type matching the  build type id " in new BuildTypesContext {
      teamcityClient.getBuildType(buildType.id) must beEqualTo(buildType)
    }
  }

  "get build types by vcs root id" should {
    "return a list of build types" in new BuildTypesContext {
      teamcityClient.getBuildTypesByVcsRootId(vcsRootId) must beEqualTo(buildTypes)
    }
  }

  "create build type" should {
    "return base build type" in new BuildTypesContext {
      teamcityClient.createBuildType(baseBuildType) must beEqualTo(baseBuildType)
    }
  }

  "delete build type" should {
    "delete build type" in new BuildTypesContext {
      teamcityClient.deleteBuildType(baseBuildType.id)
      there was one(httpClient).executeDelete(deleteBuildTypeUrl)
    }
  }

  "set build parameter" should {
    "invoke TC API" in new BuildTypesContext {
      teamcityClient.addBuildParameterToBuildType(baseBuildType.id, paramName, paramValue)
      there was one(httpClient).executePutPlainText(buildParameterUrl, paramValue, acceptTextPlain)
    }
  }

  "delete build parameter" should {
    "invoke TC API" in new BuildTypesContext {
      teamcityClient.deleteBuildParameter(baseBuildType.id, paramName)
      there was one(httpClient).executeDelete(buildParameterUrl)
    }
  }

  "create vcs roots" should {
    "return a list of vcs roots" in new VcsRootContext {
      teamcityClient.createVcsRoot(vcsRoot) must beEqualTo(baseVcsRoot)
      there was one(httpClient)
        .executePutPlainText(setVcsRootPropertiesUrl, vcsRoot.properties.property.head.value, acceptTextPlain)
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
    "invoke TC API" in new BuildTypesContext {
      teamcityClient.setBuildTypeVcsRootEntries(baseBuildType.id, vcsRootEntries)
      there was one(httpClient).executePut(createBuildTypeVcsRootEntriesUrl, writeObjectAsJson(vcsRootEntries))
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

    "attach template" in new TemplateContext {
      teamcityClient.attachTemplateToBuildType(baseTemplate.id, buildTypeId)
      there was one(httpClient).executePutPlainText(attachTemplateUrl, s"id:${ baseTemplate.id }", "application/json")
    }

    "detach template" in new TemplateContext {
      teamcityClient.detachTemplateToBuildType(buildTypeId)
      there was one(httpClient).executeDelete(attachTemplateUrl)
    }

  }

  "create snapshot dependency" should {
    "create snapshot dependency" in new SnapshotDependenciesContext {
      teamcityClient.setSnapshotDependency(baseBuildType.id, snapshotDependency) must beEqualTo(snapshotDependency)
    }
  }

  "get snapshot dependency" should {
    "get snapshot dependency" in new SnapshotDependenciesContext {
      teamcityClient.getSnapShotDependencies(baseBuildType.id) must beEqualTo(snapshotDependencies)
    }
  }

  "delete snapshot dependency" should {
    "delete snapshot dependency" in new SnapshotDependenciesContext {
      teamcityClient.deleteSnapshotDependency(baseBuildType.id, snapshotDependency.id)
      val url = s"$snapshotDependenciesUrl/${ snapshotDependency.id }"
      there was one(httpClient).executeDelete(url)
    }
  }

  "create build step" should {
    "create build step" in new StepContext {
      teamcityClient.addBuildStepToBuildType(baseBuildType.id, step) must beEqualTo(step)
    }
  }

  "get build step" should {
    "get build step" in new StepContext {
      teamcityClient.getBuildSteps(baseBuildType.id) must beEqualTo(steps)
    }
  }

  "delete build step" should {
    "delete build step" in new StepContext {
      teamcityClient.deleteBuildStep(baseBuildType.id, step.id)
      val url = s"$buildStepUrl/${ step.id }"
      there was one(httpClient).executeDelete(url)
    }
  }


  "add trigger to build type" should {
    "add the trigger" in new TriggerContext {
      teamcityClient.addTriggerToBuildType(buildTypeId, _trigger)
      there was one(httpClient).executePost(addTriggerUrl, writeObjectAsJson(_trigger))
    }
  }

  "delete trigger to build type" should {
    "delete the trigger" in new TriggerContext {
      teamcityClient.deleteTriggerFromBuildType(buildTypeId, _trigger.id)
      there was one(httpClient).executeDelete(deleteTriggerUrl)
    }
  }

  "create user" should {
    "create user" in new UserContext {
      teamcityClient.createUser(baseUser) must beEqualTo(baseUser)
    }
  }

  "get users" should {
    "get a list of users" in new UserContext {
      teamcityClient.getUsers() must beEqualTo(users)
    }
  }

  "get user by id" should {
    "get the user" in new UserContext {
      teamcityClient.getUserById(user.id) must beEqualTo(user)
    }
  }

  "get user" should {
    "get a list of users" in new UserContext {
      teamcityClient.deleteUser(user.id)
      there was one(httpClient).executeDelete(userWithIdUrl)
    }
  }

  "pause build" should {
    "invoke TC API" in new BuildTypesContext {
      teamcityClient.pauseBuild(buildType.id, pause = true)
      there was one(httpClient).executePutPlainText(setPauseBuildUrl, "true", acceptTextPlain)
    }
  }

  "get agents" should {
    "return a list of all agents" in new AgentContext {
      teamcityClient.getAgents() must beEqualTo(agents)
    }
  }

  "authorise agent" should {
    "authorize the agent " in new AgentContext {
      teamcityClient.authorizeAgent(agentId, authorize = true)
      there was one(httpClient).executePutPlainText(authorizeAgentUrl, "true", acceptTextPlain)
    }
  }

  "get build types by name" should {
    "return a build type" in new BuildTypesContext {
      teamcityClient.getBuildTypeByName(buildType.name) must beEqualTo(buildType)
    }
  }

  "get vcs roots by project" should {
    "return a vcs root" in new VcsRootContext {
      teamcityClient.getVcsRootsByProjectId(baseProject.id) must beEqualTo(vcsRoots)
    }
  }

  "get authorized agents" should {
    "return agents" in new AgentContext {
      teamcityClient.getAuthorizedAgents() must beEqualTo(agents)
    }
  }

  "get agent by id" should {
    "return agent" in new AgentContext {
      teamcityClient.getAgentById(baseAgent.id) must beEqualTo(agent)
    }
  }

  "set agent enabled" should {
    "invoke TC API" in new AgentContext {
      teamcityClient.setAgentEnabled(baseAgent.id, isEnable = true)
      there was one(httpClient).executePutPlainText(setAgentEnabledUrl, "true", acceptTextPlain)
    }
  }

  "get build queue" should {
    "return build queue" in new BuildContext {
      teamcityClient.getBuildsInQueue() must beEqualTo(Builds(1, List(baseBuild)))
    }
  }

  "add build to queue" should {
    "invoke TC API" in new BuildContext {
      teamcityClient.addToQueue(baseBuild.buildTypeId, None) must beEqualTo(build)
    }
  }

  "get build" should {
    "return relevant build" in new BuildContext {
      teamcityClient.getBuild(baseBuild.id) must beEqualTo(build)
    }
  }

  "get running builds" should {
    "return relevant build" in new BuildContext {
      teamcityClient.getRunningBuilds() must beEqualTo(Builds(1, List(baseBuild)))
    }
  }

}
