# teamcity-client

## Introduction
Teamcity client is a simple scala library which wraps REST calls to the Teamcity REST API. Use this library to perform different operations and get data from the Teamcity server. This version is tested with Teamcity version **2018.1.5 (build 58744)**. This library can be used also with Java but will require to use the **scala.collection.JavaConverters** when using the collections.

## Getting Started
Create an instance of **HttpClient** (teamcity-client uses a HttpClientWrapper to scalaj Http as an http client, you can use any http client by implementing the HttpClient trait/interface). Pass username, password and optinally timemout (default is 5 sec).
Pass an instance of **HttpClient** to the TeamCityClient,  the base url of the Teamcity server and you are ready to go.

Scala:
```scala
val timeout = 10000 //10 sec
val teamcityBaseUrl = "http://localhost:8111"
val httpClient = new HttpClientWrapper("MyUserName", "MyPassword", timeout)
val teamcityClient = new TeamCityClient(httpClient, teamcityBaseUrl)
```
Java:
```java
String baseUrl = "http://localhost:8111";
int timeout = 10000;
HttpClientWrapper httpClient = new HttpClientWrapper("MyUserName", "MyPassword", timeout);
TeamCityClient teamcityClient = new TeamCityClient(httpClient,baseUrl);
```

TeamCityClient will throw a RuntimeException containing the HTTP status code and the error message from the teamcity server if the status code is not 2XX.

## Examples
