package com.wix.ci.teamcity.client

import com.fasterxml.jackson.annotation.JsonProperty

case class Property(name: String, value: String)

case class Properties(properties: Seq[Property])

case class BaseTemplate(id: String, name: String, href: String, projectName: String, projectId: String)

case class BaseProject(id: String, name: String, href: String, webUrl: String, description: Option[String], archived: Boolean, parentProjectId: Option[String])

case class Projects(count: Int, project: List[BaseProject])

case class BaseBuildType(id: String,
                         name: String,
                         description: String,
                         template: BaseTemplate,
                         projectName: String,
                         projectId: String)

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
                   defaultTemplate: Option[Template] = None)


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