# teamcity-client

## Introduction
Teamcity client is a wrapper for Teamcity REST API calls for scala. It is a simple library that makes it easy to perform different operations and get data from the Teamcity server.

Current version is tested with Teamcity version **2018.1.5 (build 58744)**. Teamcity client can also be used with Java but will require **scala.collection.JavaConverters** when using collections (see examples below).

Since Teamcity API only returns a small set of properties for each entity when getting a collection of entities, so we mapped these to `Base{Entity}`. You can use the `id` returned in `Base{Entity}` to retrieve the full properties of any select entity (by calling `get{Entity}ById` methods).

The collection entities of TeamCity API contain a count property and a list property, and the list property name is in singular, not plural form - in order to stay as close to the API as possible, Teamcity client also follows this convention.

Please note that not all API calls and objects are covered in this README.

## Setup
Add the following dependency to you project's pom.xml:

```xml
<dependency>
    <groupId>com.wix.ci</groupId>
    <artifactId>teamcity-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Getting Started
Create an instance of Teamcity client (using the default scalaj http client).

Scala:
```scala
val timeout = 10000 //10 sec
val teamcityBaseUrl = "http://localhost:8111"
val teamcityClient = TeamCityClient.aTeamCityClient(teamcityBaseUrl, timeout, "MyUserName", "MyPassword")
```
Java:
```java
String baseUrl = "http://localhost:8111";
int timeout = 10000;
TeamCityClient teamcityClient = TeamCityClient.aTeamCityClient(teamcityBaseUrl, timeout, "MyUserName", "MyPassword")
```

Alternatively, you can create an instance of **HttpClient**. Teamcity client uses HttpClientWrapper to wrap scalaj Http as an http client, so you can use any http client by implementing the HttpClient trait/interface. Pass username, password and optionally timeout (defaults to 5 seconds). Now pass an instance of **HttpClient** to the TeamCityClient, specify the base url of the Teamcity server and you're good to go.

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

Teamcity client will throw a RuntimeException containing the HTTP status code and the error message from the Teamcity server if the status code is not 2XX.
You can then handle different http status codes, for example:
Scala:
```scala
 Try(teamcityClient.createBuildType(baseBuildType)).recover({
    case e : TeamcityServerException => {
       e.code match{
          case 404 => //do something here
          case 500 => //do something here
       }
    }
 })
```
java:
```java
  try{
      teamcityClient.createBuildType(baseBuildType);
  }catch(TeamcityServerException e){
       switch(e.code()){
           case 404: {
               //do something here
               break;
           }case 500: {
                //do something here
           }
        }
   }
```

## Examples
### Getting Teamcity server details
Scala:
```scala
val serverDetails = teamcityClient.getTeamCityServerDetails  //returns version, start time, and additional info
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
   //do some more stuff here
})
```
Java:
```java
scala.collection.JavaConverters.seqAsJavaList(teamcityClient.getProjects().project()).stream().forEach(p -> {
   System.out.println(p.toString() ) ;
   //do some more stuff here
});
```
### Get all projects with all project properties
Scala:
```scala
 val projectsWithAllProperties = teamcityClient.getProjects.project.map(p => teamcityClient.getProjectById(p.id))
```
Java:
```java
 List<Project> projectsWithAllProps = scala.collection.JavaConverters.
     seqAsJavaList(teamcityClient.getProjects().project()).stream().map(p -> {
        return teamcityClient.getProjectById(p.id());
 }).collect(Collectors.toList());
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
### Add trigger to a buildType
In some cases, like with triggers, Teamcity will ignore the trigger id and will autogenerate an id for you. So when adding a trigger to a buildType, the client will return a trigger object with the generated id.

Scala:
```scala
val triggerToAdd = Trigger("triggerIdWillBeReplacedByTC", "VCS Trigger", Properties(Nil))
val trigger = teamcityClient.addTriggerToBuildType(baseBuildType.id, triggerToAdd)

```
Java:
```java
List<Property> props = new ArrayList<>();
Trigger triggerToAdd = new Trigger("triggerIdWillBeReplacedByTC", "VCS Trigger", new Properties(scala.collection.JavaConverters.asScalaBuffer(props).toList()));
Trigger trigger = teamcityClient.addTriggerToBuildType(baseBuildType.id, triggerToAdd);
```

### Authorize all agents
Scala:
```scala
teamcityClient.getAgents().agent.foreach(a => teamcityClient.authorizeAgent(a.id,true))
```
Java:
```java
Agents allAgents = teamcityClient.getAgents();
scala.collection.JavaConverters.seqAsJavaList(allAgents.agent()).forEach(a -> {
    teamcityClient.authorizeAgent(a.id(),true);
});
```

### Get last build by status
Scala:
```scala
val lastSuccessfulBuild = teamcityClient.getLastBuildByStatus(baseBuildType.id,"success")
val lastSFailedBuild = teamcityClient.getLastBuildByStatus(baseBuildType.id,"failure")
```
Java:
```java
BaseBuild lastSuccessfulBuild = teamcityClient.getLastBuildByStatus(baseBuildType.id,"success");
BaseBuild lastSFailedBuild = teamcityClient.getLastBuildByStatus(baseBuildType.id,"failure");
```
If there's no build with requested status the server returns 404 and the client will throw an exception.

### Contributing
IT tests require docker up and running on the machine as the tests load a dockerized teamcity to run the IT tests against.
