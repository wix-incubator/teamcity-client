package com.wix.ci.teamcity.client

import com.fasterxml.jackson.annotation.JsonProperty

case class Property(name: String, value: String)

case class Properties(property: List[Property])

case class BaseTemplate(id: String, name: String, href: Option[String], projectId: String, projectName: String)

case class BaseProject(id: String, name: String, href: Option[String], webUrl : Option[String], description: Option[String], archived: Boolean, parentProjectId: Option[String])

case class Projects(count: Int, project: List[BaseProject])

case class BaseBuildType(id: String,
                         name: String,
                         description: Option[String],
                         template: Option[BaseTemplate],
                         projectName: String,
                         projectId: String,
                         paused : Boolean)

case class BuildTypes(count: Int, buildType: List[BaseBuildType])

case class Parameters(count: Int, href: String, property: List[Property])

case class Template(id: String, name: String, href: Option[String], projectId: String, projectName: String, project : BaseProject, inherited: Boolean, templateFlag: Boolean = true)

case class Project(id: String,
                   name: String,
                   parentProjectId: String,
                   href: String,
                   webUrl: String,
                   projects: Projects,
                   parentProject: BaseProject,
                   buildTypes: BuildTypes,
                   defaultTemplate: Option[Template] = None,
                   templates : Option[Templates] = None)


case class BaseVcsRoot(id: String, name: String, href: Option[String])

case class VcsRoot(id: String,
                   name: String,
                   vcsName: String,
                   href: String,
                   status: Option[String],
                   lastChecked: Option[String],
                   project: BaseProject,
                   properties: Properties)

case class VcsRoots(count: Int, href : Option[String], @JsonProperty("vcs-root")vcsRoot:Option[ List[BaseVcsRoot]])

case class Templates(count : Int,  buildType : Option[List[BaseTemplate]])

case class VcsRootEntry(id : String, @JsonProperty("checkout-rules")checkoutRules : String, @JsonProperty("vcs-root")baseVcsRoot : BaseVcsRoot)

case class VcsRootEntries(count : Int, vcsRootEntry : Option[List[VcsRootEntry]])

case class TeamCityServerDetails(buildNumber : String,
                                 buildDate : String,
                                 version : String,
                                 versionMajor : Int,
                                 versionMinor : Int,
                                 currentTime : String,
                                 startTime : String)


case class SnapshotDependency(id : String ,`type` : String, properties : Properties, @JsonProperty("source-buildType")sourceBuildType : BaseBuildType)

case class SnapshotDependencies(count : Int, @JsonProperty ("snapshot-dependency") snapshotDependency : Option[List[SnapshotDependency]])

case class Step(id : String, name : String, `type` : String, properties : Properties)

case class Steps(count : Int , step : Option[List[Step]])

case class Role(roleId : String, scope : String, href : Option[String])

case class Roles(count : Int, role : Option[List[Role]])

case class Group(key : String, name : String, href : Option[String], description : Option[String])

case class Groups(count : Int, group : Option[List[Group]])

case class BaseUser(id : Int, username : String, name : Option[String], href : Option[String])

case class User(id : Int,
                username : String,
                name : Option[String],
                email : Option[String],
                lastLogin : Option[String],
                href : Option[String],
                roles : Roles,
                groups : Groups)

case class Users(count : Int, user : Option[List[BaseUser]])

