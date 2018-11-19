package org.apache.james.gatling.jmap.scenari.common

import scala.concurrent.duration._
import java.net.URL

object Configuration {

  val ServerHostName = "127.0.0.1"
  val BaseJmapUrl = s"http://$ServerHostName:80"
  val BaseJamesWebAdministrationUrl = new URL(s"http://$ServerHostName:8000")

  val ScenarioDuration = 1 hours
  val UserCount = 300
  val RandomlySentMails = 50
  val NumberOfMailboxes = 20
  val NumberOfMessages = 100

}
