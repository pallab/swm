package com.swym.test.rules

import akka.actor.{Actor, ActorSystem, Props}
import com.swym.test.actions.ActionHandler
import com.swym.test.memorydb.TweetsTable
import com.swym.test.types.{Rule, Tweet}

object SqlRule {
  def ref(system: ActorSystem) = {
    system.actorOf(Props(new SqlRule()), s"SqlRule")
  }
}

class SqlRule extends Actor {
 def receive = {
   case t : Tweet => {
     TweetsTable.insert(t)

     val matchingRules = RulesEngine.allRules.filter(r => r.on == "tweet" && r.aggregation.isEmpty)
       .filter(r => TweetsTable.matchesCondition(t.id, r.condition))

     matchingRules.foreach{ mr =>
       println(s"MATCHED : $t <-> $mr")
       mr.actions.foreach(ActionHandler.dispatch(t, _))
     }

     TweetsTable.delete(t.id)
   }
 }
}
