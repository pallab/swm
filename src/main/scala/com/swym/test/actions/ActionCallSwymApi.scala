package com.swym.test.actions

import com.swym.test.types.{Content, FbPost, Tweet}

object ActionCallSwymApi {
  def run( c: Content) = {
    c match {
      case t : Tweet =>println(s"ACTION : ${t} : Calling 3rd party SWYM API")
      case _ => throw new Exception(s"Content ${c} not supported")
    }
  }
}
