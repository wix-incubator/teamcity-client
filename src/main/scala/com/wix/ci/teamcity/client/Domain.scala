package com.wix.ci.teamcity.client

import com.fasterxml.jackson.annotation.JsonProperty

case class Property(name: String, value: String)

case class Properties(property: List[Property])

case class BaseTemplate(id: String, name: String, href: Option[String], projectId: String, projectName: String)

case class BaseProject(id: String,
                       name: String,
                       href: Option[String],
                       webUrl: Option[String],
                       description: Option[String],
                       archived: Boolean,
                       parentProjectId: Option[String])

case class Projects(count: Int, project: List[BaseProject])

case class BaseBuildType(id: String,
                         name: String,
                         description: Option[String],
                         template: Option[BaseTemplate],
                         projectName: String,
                         projectId: String,
                         paused: Boolean)

case class BuildTypes(count: Int, buildType: List[BaseBuildType])

case class Parameters(count: Int, href: String, property: List[Property])

case class Template(id: String,
                    name: String,
                    href: Option[String],
                    projectId: String,
                    projectName: String,
                    project: BaseProject,
                    inherited: Boolean,
                    templateFlag: Boolean = true)

case class Project(id: String,
                   name: String,
                   parentProjectId: String,
                   href: String,
                   webUrl: String,
                   projects: Projects,
                   parentProject: BaseProject,
                   buildTypes: BuildTypes,
                   defaultTemplate: Option[Template] = None,
                   templates: Option[Templates] = None)


case class BaseVcsRoot(id: String, name: String, href: Option[String])

case class VcsRoot(id: String,
                   name: String,
                   vcsName: String,
                   href: String,
                   status: Option[String],
                   lastChecked: Option[String],
                   project: BaseProject,
                   properties: Properties)

case class VcsRoots(count: Int, href: Option[String], @JsonProperty("vcs-root") vcsRoot: Option[List[BaseVcsRoot]])

case class Templates(count: Int, buildType: Option[List[BaseTemplate]])

case class VcsRootEntry(id: String,
                        @JsonProperty("checkout-rules") checkoutRules: String,
                        @JsonProperty("vcs-root") baseVcsRoot: BaseVcsRoot)

case class VcsRootEntries(count: Int, @JsonProperty("vcs-root-entry") vcsRootEntry: Option[List[VcsRootEntry]])

case class TeamCityServerDetails(buildNumber: String,
                                 buildDate: String,
                                 version: String,
                                 versionMajor: Int,
                                 versionMinor: Int,
                                 currentTime: String,
                                 startTime: String)


case class SnapshotDependency(id: String,
                              `type`: String,
                              properties: Properties,
                              @JsonProperty("source-buildType") sourceBuildType: BaseBuildType)

case class SnapshotDependencies(count: Int,
                                @JsonProperty("snapshot-dependency") snapshotDependency: Option[List[SnapshotDependency]])

case class Step(id: String, name: String, `type`: String, properties: Properties)

case class Steps(count: Int, step: Option[List[Step]])

case class Role(roleId: String, scope: String, href: Option[String])

case class Roles(count: Int, role: Option[List[Role]])

case class Group(key: String, name: String, href: Option[String], description: Option[String])

case class Groups(count: Int, group: Option[List[Group]])

case class BaseUser(id: Int, username: String, name: Option[String], href: Option[String])

case class User(id: Int,
                username: String,
                name: Option[String],
                email: Option[String],
                lastLogin: Option[String],
                href: Option[String],
                roles: Roles,
                groups: Groups)

case class Users(count: Int, user: Option[List[BaseUser]])

case class Feature(id: String, `type`: String, properties: Properties)

case class Features(count: Int, feature: Option[List[Feature]])

case class Trigger(id: String, `type`: String, properties: Properties)

case class Triggers(count: Int, trigger: Option[List[Trigger]])

case class BuildType(@JsonProperty("id") id: String,
                     @JsonProperty("name") name: String,
                     @JsonProperty("templateFlag") templateFlag: Boolean,
                     @JsonProperty("description") description: Option[String],
                     @JsonProperty("projectName") projectName: String,
                     @JsonProperty("projectId") projectId: String,
                     @JsonProperty("href") href: Option[String],
                     @JsonProperty("webUrl") webUrl: Option[String],
                     @JsonProperty("project") project: Option[BaseProject],
                     @JsonProperty("template") template: Option[BaseTemplate],
                     @JsonProperty("templates") templates: Option[Templates],
                     @JsonProperty("triggers") triggers: Option[Triggers],
                     @JsonProperty("steps") steps: Option[Steps],
                     @JsonProperty("vcs-root-entries") vcsRootEntries: Option[VcsRootEntries],
                     @JsonProperty("settings") settings: Option[Properties],
                     @JsonProperty("parameters") parameters: Option[Properties],
                     @JsonProperty("features") features: Option[Features],
                     @JsonProperty("paused") paused: Boolean,
                     @JsonProperty("snapshot-dependencies") snapshotDependencies: Option[SnapshotDependencies])

case class BaseAgent(@JsonProperty("id") id: Int,
                     @JsonProperty("name") name: String,
                     @JsonProperty("typeId") typeId: Int)

case class Agents(count: Int, agent: List[BaseAgent])

case class Agent(@JsonProperty("id") id: Int,
                 @JsonProperty("name") name: String,
                 @JsonProperty("typeId") typeId: Int,
                 @JsonProperty("ip") ip: String,
                 @JsonProperty("uptodate") uptodate: Boolean,
                 @JsonProperty("enabled") enabled: Boolean,
                 @JsonProperty("connected") connected: Boolean,
                 @JsonProperty("authorized") authorized: Boolean,
                 @JsonProperty("properties") properties: Properties)

case class BaseBuild(@JsonProperty("id") id: String,
                     @JsonProperty("buildTypeId") buildTypeId: String,
                     @JsonProperty("number") number: String,
                     @JsonProperty("status") status: String,
                     @JsonProperty("state") state: String)

case class Build(@JsonProperty("id") id: String,
                 @JsonProperty("buildTypeId") buildTypeId: String,
                 @JsonProperty("history") history: Boolean,
                 @JsonProperty("number") number: String,
                 @JsonProperty("status") status: String,
                 @JsonProperty("state") state: String,
                 @JsonProperty("buildType") buildType: BaseBuildType,
                 @JsonProperty("statusText") statusText: String,
                 @JsonProperty("comment") comment: Comment,
                 @JsonProperty("running") running: Boolean,
                 @JsonProperty("queuedDate") queuedDate: String,
                 @JsonProperty("startDate") startDate: String,
                 @JsonProperty("finishDate") finishDate: String,
                 @JsonProperty("lastChanges") lastChanges: Changes,
                 @JsonProperty("agent") agent: BaseAgent,
                 @JsonProperty("running-info") runningInfo: RunningInfo,
                 @JsonProperty("revisions") revisions: Revisions,
                 @JsonProperty("properties") properties: Properties,
                 @JsonProperty("branchName") branchName: String)

case class Comment(@JsonProperty("user") user: User,
                   @JsonProperty("timestamp") timestamp: String,
                   @JsonProperty("text") text: String)

case class Changes(@JsonProperty("count") count: Int,
                   @JsonProperty("change") change: List[BaseChange])

case class BaseChange(@JsonProperty("id") id: String,
                      @JsonProperty("version") version: String,
                      @JsonProperty("username") username: String,
                      @JsonProperty("date") date: String)

case class RunningInfo(@JsonProperty("percentageComplete") percentageComplete: String,
                       @JsonProperty("elapsedSeconds") elapsedSeconds: String,
                       @JsonProperty("estimatedTotalSeconds") estimatedTotalSeconds: String,
                       @JsonProperty("currentStageText") currentStageText: String,
                       @JsonProperty("outdated") outdated: Boolean,
                       @JsonProperty("probablyHanging") probablyHanging: Boolean)

case class Revisions(@JsonProperty("revision") revisions: List[Revision])

case class Revision(@JsonProperty("version") version: String)
