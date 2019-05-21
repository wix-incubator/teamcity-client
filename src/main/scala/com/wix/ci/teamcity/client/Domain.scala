package com.wix.ci.teamcity.client

import com.fasterxml.jackson.annotation.JsonProperty

case class Property(name: String, value: String)

case class Properties(property: List[Property])

case class BaseTemplate(id: String, name: String, href: String, projectName: String, projectId: String)

case class BaseProject(id: String, name: String, href: String, webUrl: String, description: Option[String], archived: Boolean, parentProjectId: Option[String])

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

case class Template(id: String, name: String, href: String, projectId: String, ProjectName: String, inherited: Boolean, templateFlag: Boolean)

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


case class BaseVcsRoot(id: String, name: String, href: String)

case class VcsRoot(id: String,
                   name: String,
                   vcsName: String,
                   href: String,
                   status: Option[String],
                   lastChecked: Option[String],
                   project: BaseProject,
                   properties: Properties)

case class VcsRoots(count: Int, href : String, @JsonProperty("vcs-root")vcsRoot:Option[ List[BaseVcsRoot]])

case class Templates(count : Int,  template : Option[List[BaseTemplate]])

case class VcsRootEntry(id : String, @JsonProperty("checkout-rules")checkoutRules : String, @JsonProperty("vcs-root")baseVcsRoot : BaseVcsRoot)

case class VcsRootEntries(count : Int, vcsRootEntry : Option[List[VcsRootEntry]])

