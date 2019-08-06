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
      teamcityClient.getVcsRoots() must beEqualTo(VcsRoots(0, Some("/httpAuth/app/rest/vcs-roots"), None))
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

    "set build type root entries" in new Context {
      initializeProjAndBuildTypes(1)
      teamcityClient.createVcsRoot(vcsRoot)
      teamcityClient.setBuildTypeVcsRootEntries(baseBuildType.id, vcsRootEntries)

      teamcityClient.getBuildType(baseBuildType.id)
        .vcsRootEntries
        .get
        .vcsRootEntry
        .get
        .contains(vcsRootEntries.vcsRootEntry.get.head) must beTrue


      cleanupProjAndBuildTypes(1)
      teamcityClient.deleteVcsRoot(vcsRoot.id)
    }

    "set and delete build parameter" in new Context {
      initializeProjAndBuildTypes(1)

      teamcityClient.addBuildParameterToBuildType(baseBuildType.id, paramName, paramValue)
      teamcityClient.getBuildType(baseBuildType.id).parameters.get.property
        .contains(Property(paramName, paramValue)) must beTrue

      teamcityClient.deleteBuildParameter(baseBuildType.id, paramName)
      teamcityClient.getBuildType(baseBuildType.id).parameters.get.property
        .contains(Property(paramName, paramValue)) must beFalse

      cleanupProjAndBuildTypes(1)
    }


    "creates template retrieve it" in new Context{
      val res = teamcityClient.createTemplate(baseTemplate)
      res must beEqualTo(template)
      teamcityClient.getTemplates must beEqualTo(templates)
      teamcityClient.deleteTemplate(baseTemplate.id)
      teamcityClient.getTemplates must beEqualTo(Templates(0,Some(List())))
    }

    "get team city server details" in new Context{
      teamcityClient.getTeamCityServerDetails().copy(currentTime = "",startTime = "") must beEqualTo(teamCityServerDetails)
    }

    "create snapshot dependency" in new Context{
      initializeProjAndBuildTypes(2)

      teamcityClient.setSnapshotDependency(baseBuildType.id,dependency) must beEqualTo(dependencyWithDefaultProps)
      teamcityClient.getSnapShotDependencies(baseBuildType.id) must beEqualTo(snapshotDependencies)
      teamcityClient.deleteSnapshotDependency(baseBuildType.id,dependencyWithDefaultProps.id)
      teamcityClient.getSnapShotDependencies(baseBuildType.id) must beEqualTo(SnapshotDependencies(0,None))

      cleanupProjAndBuildTypes(2)
    }

    "create step" in new Context{
      initializeProjAndBuildTypes(1)

      teamcityClient.addBuildStepToBuildType(baseBuildType.id,step) must beEqualTo(step.copy(id=stepId))
      teamcityClient.getBuildSteps(baseBuildType.id) must beEqualTo(steps)
      teamcityClient.deleteBuildStep(baseBuildType.id,stepId)
      teamcityClient.getBuildSteps(baseBuildType.id) must beEqualTo(Steps(0,None))

      cleanupProjAndBuildTypes(1)
    }

    "create user retrieve him and then delete him" in new Context{
      teamcityClient.createUser(baseUser) must beEqualTo(baseUser)
      teamcityClient.getUsers() must beEqualTo(users)
      teamcityClient.getUserById(baseUser.id) must beEqualTo(user)
      teamcityClient.deleteUser(baseUser.id)
      teamcityClient.getUsers() must beEqualTo(Users(1,Option(List(baseUserAdmin))))
    }

    "attach and detach template to build type" in new Context{
      teamcityClient.createProject(baseProject)
      teamcityClient.createBuildType(baseBuildType)
      teamcityClient.createTemplate(baseTemplate)

      teamcityClient.attachTemplateToBuildType(baseTemplate.id,baseBuildType.id)
      teamcityClient.getBuildType(baseBuildType.id).templates.head.buildType.head.head.id must beEqualTo(baseTemplate.id)

      teamcityClient.detachTemplateToBuildType(baseBuildType.id)
      teamcityClient.getBuildType(baseBuildType.id).templates.head.buildType.head must beEmpty

      cleanupProjAndBuildTypes(1)
    }

    "move project" in new Context {
      teamcityClient.createProject(baseProject)
      teamcityClient.createProject(newParentBaseProject)

      teamcityClient.getProjectById(baseProject.id).parentProject.id must beEqualTo(rootBaseProject.id)
      teamcityClient.moveProject(baseProject.id, newParentBaseProject)
      teamcityClient.getProjectById(baseProject.id).parentProject.id must beEqualTo(newParentBaseProject.id)
      teamcityClient.getProjectById(newParentBaseProject.id).projects.project.size must beEqualTo(1)
      cleanupMoveProjectTest()
    }

    "pause and unpause build type" in new Context {
      initializeProjAndBuildTypes(1)
      teamcityClient.pauseBuild(baseBuildType.id, pause = true)
      teamcityClient.getBuildType(baseBuildType.id).paused must beTrue

      teamcityClient.pauseBuild(baseBuildType.id, pause = false)
      teamcityClient.getBuildType(baseBuildType.id).paused must beFalse

      cleanupProjAndBuildTypes(1)
    }

    "add and remove triggers" in new Context {
      initializeProjAndBuildTypes(1)
      teamcityClient.getBuildType(baseBuildType.id).triggers.get.trigger must beNone

      val _trigger = teamcityClient.addTriggerToBuildType(baseBuildType.id, trigger)
      teamcityClient.getBuildType(baseBuildType.id).triggers.get.trigger.get.contains(expectedTrigger) must beTrue

      teamcityClient.deleteTriggerFromBuildType(baseBuildType.id, _trigger.id)
      teamcityClient.getBuildType(baseBuildType.id).triggers.get.trigger must beNone

      cleanupProjAndBuildTypes(1)
    }

    "get build type returns build type" in new Context {
      initializeGetBuildTypeTest()
      teamcityClient
        .getBuildType(baseBuildType.id)
        .copy(href = None, webUrl = None, settings = None, features = None) must
        beEqualTo(createExpectedBuildType("TRIGGER_2", "RUNNER_2"))

      cleanupProjAndBuildTypes(2)
      teamcityClient.deleteVcsRoot(vcsRoot.id)
    }

    "get build type by name returns build type" in new Context {
      initializeGetBuildTypeTest()
      teamcityClient.getBuildTypeByName(baseBuildType.name)
        .copy(href = None, webUrl = None, settings = None, features = None) must
        beEqualTo(createExpectedBuildType("TRIGGER_3", "RUNNER_3"))

      cleanupProjAndBuildTypes(2)
      teamcityClient.deleteVcsRoot(vcsRoot.id)
    }

    "get vcs roots by project" in new Context {
      initializeProjAndBuildTypes(1)
      teamcityClient.createVcsRoot(vcsRoot)
      teamcityClient.setBuildTypeVcsRootEntries(baseBuildType.id, vcsRootEntries)
      teamcityClient.getVcsRootsByProjectId(rootBaseProject.id).copy(href = None) must
        beEqualTo(VcsRoots(1, None, Some(List(baseVcsRoot))))
      cleanupProjAndBuildTypes(1)
    }

    "get agents returns all agents" in new Context{
      teamcityClient.getAgents().agent.size must beEqualTo(1)
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
    val stepType = "simpleRunner"
    val stepId = "RUNNER_1"
    val templateId = "template1"
    val templateName = "some template"
    val buildTypeId1 = "myBuildTypeId"
    val buildTypeId2 = "myBuildTypeId2"
    val buildTypeName1 = "my build type"
    val buildTypeName2 = "my build type2"
    val buildTypeDesc = Some("some desc")
    val vcsRootId = "somevcsroot"
    val vcsRootName = "some vcs root"
    val vcsName = "jetbrains.git"
    val paramName = "param"
    val paramValue = "value"

    val httpClient = new HttpClientWrapper(username, password)
    val teamcityClient = new TeamCityClient(httpClient, teamcityBaseUrl)
    val rootBaseProject = BaseProject(rootProjectId,rootProjectName, Some("/httpAuth/app/rest/projects/id:_Root"), Some("http://localhost:8111/project.html?projectId=_Root"), Some("Contains all other projects"), archived = false, None)

    val property = Property("ignoreKnownHosts", "true")
    val baseProject = BaseProject(projectId, projectName, Some("/httpAuth/app/rest/projects/id:projid"), Some("http://localhost:8111/project.html?projectId=projid"), Some("projDesc"), archived = false, Some(rootProjectId))
    val newParentBaseProject = BaseProject("parentProjectId", "parentProjectName", Some("/httpAuth/app/rest/projects/id:projid"), Some("http://localhost:8111/project.html?projectId=projid"), Some("projDesc"), archived = false, Some(rootProjectId))
    val project = Project(baseProject.id, baseProject.name, baseProject.parentProjectId.get, baseProject.href.get, baseProject.webUrl.get, Projects(0, null), rootBaseProject, BuildTypes(0, List()), templates = Some(Templates(0, Option(List()))))
    val vcsRoot = VcsRoot(vcsRootId, vcsRootName, vcsName, "/httpAuth/app/rest/vcs-roots/id:somevcsroot", None, None, rootBaseProject, Properties(List(property)))
    val baseVcsRoot = BaseVcsRoot(vcsRootId, vcsRootName, Some("/httpAuth/app/rest/vcs-roots/id:somevcsroot"))
    val vcsRoots = VcsRoots(1, Some("/httpAuth/app/rest/vcs-roots"), Some(List(baseVcsRoot)))

    val baseBuildType = BaseBuildType(buildTypeId1, buildTypeName1, buildTypeDesc, None, projectName, projectId, paused = false)
    val baseBuildType2 = BaseBuildType(buildTypeId2, buildTypeName2, buildTypeDesc, None, projectName, projectId, paused = false)
    val buildTypes = BuildTypes(2, List(baseBuildType.copy(description = None), baseBuildType2.copy(description = None)))
    val vcsRootEntries = VcsRootEntries(1,Some(List(VcsRootEntry(baseVcsRoot.id,"some checkout rules",baseVcsRoot))))

    val baseTemplate = BaseTemplate(templateId, templateName,Some("/httpAuth/app/rest/buildTypes/id:template1"),rootProjectId,rootProjectName)
    val template = Template(
      templateId,
      templateName,
      Some("/httpAuth/app/rest/buildTypes/id:template1"),
      rootProjectId,
      rootProjectName,
      rootBaseProject,
      inherited = false)
    val templates = Templates(1,Some(List(baseTemplate)))
    val teamCityServerDetails = TeamCityServerDetails("58744","20181218T000000+0000","2018.1.5 (build 58744)",2018,1,"","")
    val dependency = SnapshotDependency("not-important","snapshot_dependency",Properties(List()),baseBuildType2)

    val defaultCreatedDependencyProps = Properties(List(Property("run-build-if-dependency-failed","MAKE_FAILED_TO_START"),
      Property("run-build-if-dependency-failed-to-start","MAKE_FAILED_TO_START"),
      Property("run-build-on-the-same-agent","false"),
      Property("take-started-build-with-same-revisions","false"),
      Property("take-successful-builds-only","false")))
    val dependencyWithDefaultProps = dependency.copy(properties = defaultCreatedDependencyProps,id=baseBuildType2.id,sourceBuildType = baseBuildType2.copy(description = None))

    val snapshotDependencies = SnapshotDependencies(1, Some(List(dependencyWithDefaultProps)))

    val stepProperties = Properties(List(Property("teamcity.step.mode","default")))
    val step = Step("not-important","maven step",stepType,stepProperties)
    val steps = Steps(1,Option(List(step.copy(id=stepId))))
    val autoGeneratedStepsIdWith = (id: String) => Steps(1,Option(List(step.copy(id = id))))

    val defaultGroup = Group("ALL_USERS_GROUP","All Users",Some("/httpAuth/app/rest/userGroups/key:ALL_USERS_GROUP"),Some("Contains all TeamCity users"))
    val groups = Groups(1,Some(List(defaultGroup)))
    val baseUser = BaseUser(2,"username1",Some("name1"),Some("/httpAuth/app/rest/users/id:2"))
    val baseUserAdmin = BaseUser(1,"admin",None,Some("/httpAuth/app/rest/users/id:1"))
    val users = Users(2,Some(List(baseUserAdmin,baseUser)))
    val user = User(2,"username1",Some("name1"),None,None,Some("/httpAuth/app/rest/users/id:2"),Roles(0,Option(List())),groups)

    val trigger = Trigger("triggerIdWillBeReplacedByTC", "VCS Trigger", Properties(Nil))
    val expectedTrigger = Trigger("TRIGGER_1", "VCS Trigger", null)
    val noAgents = Agents(0,List())

    def initializeProjAndBuildTypes(numberOfBuildTypes : Int): Unit ={
      teamcityClient.createProject(baseProject)
      numberOfBuildTypes match{
        case 1 => teamcityClient.createBuildType(baseBuildType)
        case 2 => teamcityClient.createBuildType(baseBuildType)
                  teamcityClient.createBuildType(baseBuildType2)
      }
    }

    def cleanupMoveProjectTest(): Unit = {
      teamcityClient.deleteProject(baseProject.id)
      teamcityClient.deleteProject(newParentBaseProject.id)
    }

    def cleanupProjAndBuildTypes(numberOfBuildTypes : Int): Unit ={
      numberOfBuildTypes match{
        case 1 => teamcityClient.deleteBuildType(baseBuildType.id)
        case 2 => teamcityClient.deleteBuildType(baseBuildType.id)
                  teamcityClient.deleteBuildType(baseBuildType2.id)
      }
      teamcityClient.deleteProject(baseProject.id)
    }

    def initializeGetBuildTypeTest() = {
      teamcityClient.deleteTemplate(baseTemplate.id)
      teamcityClient.createProject(baseProject)
      teamcityClient.createBuildType(baseBuildType)
      teamcityClient.createBuildType(baseBuildType2) //for the snapshot dependencies
      teamcityClient.createTemplate(baseTemplate)
      teamcityClient.attachTemplateToBuildType(baseTemplate.id, baseBuildType.id)
      teamcityClient.addTriggerToBuildType(baseBuildType.id, trigger)
      teamcityClient.addBuildStepToBuildType(baseBuildType.id, step)
      teamcityClient.addBuildParameterToBuildType(baseBuildType.id, paramName, paramValue)
      teamcityClient.createVcsRoot(vcsRoot)
      teamcityClient.setBuildTypeVcsRootEntries(baseBuildType.id, vcsRootEntries)
      teamcityClient.setSnapshotDependency(baseBuildType.id,dependency)
    }

    def createExpectedBuildType(triggerId: String, stepId: String): BuildType= {
      val templateFlag = false
      val href: Option[String] = None
      val webUrl: Option[String] = None
      val templates: Option[Templates] = Some(Templates(1, Some(List(baseTemplate))))
      val triggers: Option[Triggers] = Some(Triggers(1, Some(List(expectedTrigger.copy(id = triggerId)))))
      val buildSteps: Option[Steps] = Some(autoGeneratedStepsIdWith(stepId))
      val setting: Option[Properties] = None
      val params: Option[Properties] = Some(Properties(List(Property(paramName, paramValue))))
      val features: Option[Features] = None
      val dependencies : Option[SnapshotDependencies] = Some(SnapshotDependencies(1,Some(List(dependencyWithDefaultProps))))

      BuildType(
        baseBuildType.id,
        baseBuildType.name,
        templateFlag,
        None,
        baseBuildType.projectName,
        baseBuildType.projectId,
        href,
        webUrl,
        Option(baseProject),
        None,
        templates,
        triggers,
        buildSteps,
        Option(vcsRootEntries),
        setting,
        params,
        features,
        paused = false,
        dependencies)
    }
  }

}
