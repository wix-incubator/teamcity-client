package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.Role

trait RoleContext extends ContextBase {

  val role = Role("roleId", "scope", Some("href"))

}