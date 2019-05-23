package com.wix.ci.teamcity.client.support

import com.wix.ci.teamcity.client._

class UserContext extends RoleContext with GroupContext with ContextBase {

  val baseUser = BaseUser(1, "username", Some("name"), Some("href"))
  val user = User(baseUser.id, baseUser.username, baseUser.name, Some("email"), Some("lastLogin"), baseUser.href, Roles(1, Some(List(role))), Groups(1, Some(List(group))))
  val users = Users(1, Some(List(baseUser)))
  val userUrl = s"$baseUrl/${TeamCityClient.contextPrefix}/users"
  val userWithIdUrl = s"$userUrl/id:${baseUser.id}"

  httpClient.executePost(userUrl, writeObjectAsJson(baseUser)) returns writeObjectAsJson(baseUser)
  httpClient.executeGet(userUrl) returns writeObjectAsJson(users)
  httpClient.executeGet(userWithIdUrl) returns writeObjectAsJson(user)
}
