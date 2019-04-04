package org.apache.james.gatling.simulation.jmap

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.apache.james.gatling.control.UserCreator
import org.apache.james.gatling.jmap.scenari.JmapAllScenario
import org.apache.james.gatling.simulation.{Configuration, HttpSettings}

class JmapAllSimulation extends Simulation {

  private val users = new UserCreator(Configuration.BaseJamesWebAdministrationUrl).createUsersWithInboxAndOutbox(Configuration.UserCount)

  private val scenario = new JmapAllScenario()

  setUp(scenario.generate(Configuration.ScenarioDuration, users).inject(atOnceUsers(Configuration.UserCount))).protocols(HttpSettings.httpProtocol)
}