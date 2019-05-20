package com.wix.ci.teamcity.client.it
import java.io.{File, InputStreamReader}

import org.apache.commons.io.{FileUtils, IOUtils}

import sys.process._
import scala.io.Source

trait ITEnv {
  private val teamcityVersion = "2018.1.5-linux"
//  private val teamcityVersion = sys.props.getOrElse("teamcity.image.version",
//    throw new RuntimeException("Please set the property 'teamcity.image.version'")
//  )

  val containerName = "TestTeamCity"
  val externalPort = 8111
  val internalPort = 8111
  val imageName = s"jetbrains/teamcity-server:$teamcityVersion"
  val logsDir = new File("./teamcity-logs")
  val dataDir = new File("./teamcity-data")



  def loadTeamcityDockerImage() = {
    s"docker pull $imageName".!
  }

  def startTeamcityDocker() = {
    if(logsDir.exists()) FileUtils.deleteQuietly(logsDir)
    if(dataDir.exists()) FileUtils.deleteQuietly(dataDir)
    logsDir.mkdirs()
    dataDir.mkdirs()
    val dirsOpt = s"-v ${dataDir.getAbsolutePath}:/data/teamcity_server/datadir -v ${logsDir.getAbsolutePath}:/opt/teamcity/logs "
   s"""docker run  -d --name $containerName  $dirsOpt -p $externalPort:$internalPort  $imageName""".stripMargin.!
  }

  def killTeamcityDocker() = {
    s"""docker rm -f $containerName
     """.stripMargin.!
  }

  def copyInitialConfigToDataDir() : Unit = {
    val in =  Source.fromResource("tc_initial_data.zip").reader()
    val byteArr = IOUtils.toByteArray(in)
   // FileUtils.writeByteArrayToFile()

  }
}
