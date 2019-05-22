# teamcity-client

## Introduction
Teamcity client is a simple scala library which wraps REST calls to the Teamcity REST API. Use this library to perform different operations and get data from the Teamcity server. This version is tested with Teamcity version **2018.1.5 (build 58744)**. This library can be used also with Java but will require to use the **scala.collection.JavaConverters** when using the collections (see in the examples below).

## Getting Started
Create an instance of **HttpClient** (teamcity-client uses a HttpClientWrapper to wrap scalaj Http as an http client, you can use any http client by implementing the HttpClient trait/interface). Pass username, password and optinally timemout (default is 5 sec).
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
### Getting Teamcity server details
Scala:
```scala
val serverDetails = teamcityClient.getTeamCityServerDetails  //returns version, start time and additional info
```
Java:
```java
TeamCityServerDetails serverDetails =  teamcityClient.getTeamCityServerDetails();
```

### Creating a project (under the default root project)
Scala:
```scala
val projectToCreate = BaseProject("myProjId", "My Proj Name", None, None, Some("projDesc"), false, Some(rootProjectId))
val createdProject = teamcityClient.createProject(projectToCreate)
```
Java:
```java
scala.Option<String> description = scala.Option.apply("this describes my project");
scala.Option<String> parentProjId = scala.Option.apply("_Root");
scala.Option<String> noHref = scala.Option.apply(null);   //this property will be filled by the server once project is created
scala.Option<String> noWebUrl = scala.Option.apply(null); //this property will be filled by the server once project is created
BaseProject projectToCreate = new BaseProject("myProjId","My Proj Name",noHref,noWebUrl,description,false,parentProjId);

BaseProject createdProject = teamcityClient.createProject(projectToCreate);
```

### Getting all projects
Scala:
```scala
teamcityClient.getProjects.project.foreach({
   println(_)
   //do some more suff here
})
```
Java:
```java
scala.collection.JavaConverters.seqAsJavaList(teamcityClient.getProjects().project()).stream().forEach(p -> {
   System.out.println(p.toString() ) ;
   //do some more suff here
});
```

### Add snapshot dependency to a buildType
Scala:
```scala
val dependencyProps = Properties(List(Property("run-build-if-dependency-failed","MAKE_FAILED_TO_START")))
val dependency = SnapshotDependency(baseBuildType.id,"snapshot_dependency",dependencyProps ,baseBuildType2)
teamcityClient.createSnapshotDependency(baseBuildType.id, dependency)
     
```
Java:
```java
BaseBuildType baseBuildType = new BaseBuildType("myBuildTypeId", "my build id",
                scala.Option.apply(null),scala.Option.apply(null),"My Proj Name","myProjId",false);
List<Property> props = new ArrayList<>();
props.add(new Property("run-build-on-the-same-agent","false"));
Properties properties = new Properties(scala.collection.JavaConverters.asScalaBuffer(props).toList() );
SnapshotDependency dependency = new SnapshotDependency("myBuildTypeId2","snapshot_dependency",properties,baseBuildType);

teamcityClient.createSnapshotDependency(baseBuildType.id(),dependency);
```        
