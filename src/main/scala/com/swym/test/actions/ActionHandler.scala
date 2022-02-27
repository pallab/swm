package com.swym.test.actions

import com.swym.test.types.Actions._
import com.swym.test.types.{ActionDef, Content, Rule, Tweet}

object ActionHandler {

  def dispatch(content: Content, action: ActionDef) = {
    action.name match {
      case CallSwym => ActionCallSwymApi.run(content)
      case AddColor => ActionAddColor.run(content, action)
      case CallFb => ActionCallFbApi.run(content)
      case AddEmoji => ActionAddEmoji.run(content, action)
      case _ => throw new Exception(s"Action type ${action.name} not supported")
    }
  }
}
