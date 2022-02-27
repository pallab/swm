package com.swym.test.actions

import com.swym.test.types.{ActionDef, Content, FbPost, Tweet}

object ActionAddColor {
  def run( c: Content, action: ActionDef) = {
    c match {
      case t : Tweet => println(s"ACTION : ${t} : Adding color ${action.params.mkString(",")}")
      case _ => throw new Exception(s"Content ${c} not supported")
    }
  }
}
