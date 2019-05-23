package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.{Properties, Property}

trait PropertiesContext extends ContextBase {

  val property = Property("propName", "value")
  val properties = Properties(List(property))

}
