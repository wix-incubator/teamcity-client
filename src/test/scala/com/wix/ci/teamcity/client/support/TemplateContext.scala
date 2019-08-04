package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{BaseTemplate, TeamCityClient, Template, Templates}

trait TemplateContext extends ContextBase {

  val baseTemplate = BaseTemplate("tempId", "tempName", Some("href"), baseProject.name, baseProject.id)
  val template = Template("tempId", "tempName", Some("href"), baseProject.id, baseProject.name, baseProject, inherited = true)
  val templates = Templates(1, Some(List(baseTemplate)))

  val buildTypeId = "bt1"
  val templateUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes/id:${baseTemplate.id}"
  val postTemplateUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/projects/id:${baseTemplate.projectId}/templates"
  val getTemplatesUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes?locator=templateFlag:true"
  val attachTemplateUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/buildTypes/$buildTypeId/template"


  httpClient.executePost(postTemplateUrl, writeObjectAsJson(baseTemplate)) returns writeObjectAsJson(template)
  httpClient.executeGet(getTemplatesUrl) returns writeObjectAsJson(templates)


}
