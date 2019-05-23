package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client.Group

trait GroupContext extends ContextBase {

  val group = Group("key", "name", Some("href"), Some("href"))

}
