package com.swym.test.rules

import akka.actor.{Actor, ActorSystem, Props}
import com.swym.test.actions.ActionHandler
import com.swym.test.memorydb.TweetCountByUserTable
import com.swym.test.types.{Aggregations, Tweet}

object CountByUser {
  def ref(system: ActorSystem) = {
    system.actorOf(Props(new CountByUser()), s"CountByUser")
  }
}

class CountByUser extends Actor {
  def receive = {
    case t: Tweet => {
      val rules = RulesEngine.allRules.filter(r => r.on == "tweet" &&
        r.aggregation == Some(Aggregations.CountByUser))

      val (count, _) = TweetCountByUserTable.incrementAndGet(t.user)

      val matchingRules = rules.filter(r =>
        TweetCountByUserTable.matchesCondition(t.user, r.condition))

      matchingRules.foreach { mr =>
        println(s"MATCHED : $t <-> $mr")
        mr.actions.foreach(ActionHandler.dispatch(t, _))
      }

      if (matchingRules.nonEmpty) TweetCountByUserTable.updateLastTriggered(t.user, count)

    }
  }
}
