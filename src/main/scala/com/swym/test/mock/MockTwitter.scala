package com.swym.test.mock

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.swym.test.types.Tweet

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt


object MockTwitter {
  case object MAKE_TWEET

  def start(system: ActorSystem, receiver: ActorRef) = {
    system.actorOf(Props(new MockTwitter(receiver)), s"MOCKTwitter")
  }

  val testTweets = Array(
    Tweet(1, "a", "Hello", "pune"),
    Tweet(2, "b", "Hello", "pune"),
    Tweet(3, "a", "Hello", "bangalore"),
    Tweet(4, "a", "Hello", "pune"),
    Tweet(5, "c", "social media", "pune"),
    Tweet(6, "d", "Hello", "delhi"),
    Tweet(7, "a", "Hello", "pune"),
    Tweet(8, "a", "Hello", "pune"),
    Tweet(9, "a", "hi", "pune")
  )
}
class MockTwitter(receiver: ActorRef) extends Actor{
  import MockTwitter._
  implicit val ctx: ExecutionContextExecutor = context.dispatcher

  context.system.scheduler.scheduleAtFixedRate(
    2.seconds, 3.second, self, MAKE_TWEET
  )
  var indx = 0
  def receive = {

    case MAKE_TWEET =>
      if(indx < testTweets.length){
        receiver ! testTweets(indx)
        indx +=1
        println(s"NEW TWEET : ${testTweets(indx-1)}")
      } else println("------------ END ---------")
  }

}
