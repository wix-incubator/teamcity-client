package com.wix.ci.teamcity.client.it
import java.io.File

import org.apache.commons.io.{FileUtils, IOUtils}

import scala.sys.process._

trait ITEnv {
  private val teamcityVersion = "2018.1.5-linux"


  val containerName = "TestTeamCity"
  val agentContainerName = "TestTeamCityAgent"
  val externalPort = 8111
  val internalPort = 8111
  val imageName = s"jetbrains/teamcity-server:$teamcityVersion"
  val logsDir = new File("./teamcity-logs")
  val dataDir = new File("./teamcity-data")
  val confZip = new File(dataDir,"tc_initial_data.zip")
  val agentImageName = "jetbrains/teamcity-minimal-agent"

  val externalAgentPort = 8110
  val internalAgentPort = 8110


  def loadTeamcityDockerImage() = {
    s"docker pull $imageName".!
    s"docker pull $agentImageName".!
  }

  def startTeamcityDocker() = {
    killTeamcityDocker()
    if(logsDir.exists()) FileUtils.deleteQuietly(logsDir)
    if(dataDir.exists()) FileUtils.deleteQuietly(dataDir)
    logsDir.mkdirs()
    dataDir.mkdirs()
    copyInitialConfigToDataDir()
    unzipConfiguration()
    val dirsOpt = s"-v ${dataDir.getAbsolutePath}:/data/teamcity_server/datadir -v ${logsDir.getAbsolutePath}:/opt/teamcity/logs "
   s"""docker run  -d --name $containerName  $dirsOpt -p $externalPort:$internalPort  $imageName""".stripMargin.!
    Thread.sleep(60000)//give teamcity time to load

    s"""docker run  -d --name $agentContainerName --link $containerName -e SERVER_URL=$containerName:8111 -p $externalAgentPort:$internalAgentPort $agentImageName""".stripMargin.!
    Thread.sleep(60000)//give agent time to load
  }

  def killTeamcityDocker() = {
    s"""docker rm -f $containerName
     """.stripMargin.!
    s"""docker rm -f $agentContainerName
     """.stripMargin.!
  }

  def copyInitialConfigToDataDir() : Unit = {
    val in =  getClass.getResourceAsStream("/tc_initial_data.zip")
    val byteArr = IOUtils.toByteArray(in)

    FileUtils.writeByteArrayToFile(confZip,byteArr)
  }

  def unzipConfiguration(): Unit = {
    val unzipCmd =  "unzip tc_initial_data.zip -d ."
    Process(unzipCmd, dataDir).!!
  }
}
