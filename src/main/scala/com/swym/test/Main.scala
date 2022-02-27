package com.swym.test

import akka.actor.{ActorSystem}
import com.swym.test.memorydb.{TweetCountByUserTable, TweetsTable}
import com.swym.test.mock.MockTwitter
import com.swym.test.rules.RulesEngine

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("swym")
  implicit val executionContext = system.dispatcher

  // create the tables
  TweetsTable.createTable
  TweetCountByUserTable.createTable

  val rulesEngine = RulesEngine.start(system)

  println("starting mock twitter")
  // a mock twitter server which sends a tweet every few seconds to the rules engine
  MockTwitter.start(system, rulesEngine)

}
