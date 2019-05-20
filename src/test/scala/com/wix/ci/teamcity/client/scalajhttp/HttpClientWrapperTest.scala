package com.wix.ci.teamcity.client.scalajhttp

import org.specs2.mutable.SpecificationWithJUnit

class HttpClientWrapperTest extends SpecificationWithJUnit {
  "create basic authentication" should{
    "create a base 64 authentication string for username and password" in{
      val wrapper = new HttpClientWrapper("some-user", "pwd1234")
      wrapper.createBasicAuthentication() must beEqualTo("c29tZS11c2VyOnB3ZDEyMzQ=")
    }
  }
}
