package com.swym.test

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

package object memorydb {
  implicit class FutureHelper[T](future : Future[T]) {
    def block(seconds: Int= 3): T = {
      Await.result(future, Duration(seconds, "seconds"))
    }
  }

  object TableNames {
    val TWEETS = "tweets"
    val TWEETS_BY_USER = "tweets_by_user"
  }
}
