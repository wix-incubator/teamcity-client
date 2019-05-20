package com.wix.ci.teamcity.client

case class Property(name : String, value : String, inherited : Boolean)
case class BaseTemplate(id : String, name : String, href : String, projectName : String, projectId : String)
case class BaseProject(id : String, name : String, href : String, webUrl : String, description : String,archived : Boolean,parentProjectId : Option[String])
case class Projects(count : Int, project : List[BaseProject])
case class BaseBuildType(id : String,
                         name : String,
                         description : String,
                         template : BaseTemplate,
                         projectName : String,
                         projectId : String)
case class BuildTypes(count : Int, buildType : List[BaseBuildType])
case class Parameters(count : Int, href : String, property : List[Property])
case class Template(id : String, name : String, href : String, projectId : String, ProjectName : String, inherited : Boolean, templateFlag : Boolean)

case class Project(id: String,
                   name: String,
                   parentProjectId : String,
                   href : String,
                   webUrl : String,
                   projects : Projects,
                   parentProject : BaseProject,
                   buildTypes : BuildTypes,
                   defaultTemplate : Option[Template] = None)