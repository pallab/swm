package com.swym.test.rules

import akka.actor.{Actor, ActorSystem, Props}
import com.swym.test.types.{Content, Rule}
import play.api.libs.json.{JsValue, Json}

object RulesEngine {

  lazy val allRules : Seq[Rule] = {
    val rulesString = scala.io.Source.fromResource("rules.json").mkString
    Json.parse(rulesString).as[List[Rule]]
  }

  def start(system: ActorSystem) = {
    system.actorOf(Props(new RulesEngine()), s"RulesEngine")
  }
}

class RulesEngine extends Actor {

  // in practice these workers my be loaded dynamically based on some configuration
  // also this is where supervision strategy will be implemented for error handling/restart etc.
  val sqlRulesActor = SqlRule.ref(context.system)
  val countByUserActor = CountByUser.ref(context.system)

  def receive = {
    case c: Content =>
      sqlRulesActor ! c
      countByUserActor ! c
  }
}
