package com.swym.test

import play.api.libs.json.Json.WithDefaultValues
import play.api.libs.json.{Format, JsString, JsSuccess, JsValue, Json, OFormat}

package object types {

  object Aggregations extends Enumeration {
    type Aggregation = Value
    val CountByUser, CountByLocation = Value
  }

  object Actions extends Enumeration {
    type Action = Value
    val CallFb, CallSwym, AddColor, AddEmoji = Value
  }

  sealed trait Content
  final case class Tweet(id: Long, user: String, text: String, location: String) extends Content
  final case class FbPost(id: Long, user: String, text: String, likes : Int) extends Content


  final case class ActionDef(name: Actions.Action, params : List[String] = List())

  final case class Rule(id : Int, on : String,
                        aggregation : Option[Aggregations.Aggregation],
                        condition : String, actions : List[ActionDef])



  implicit val AggregationsFormat = new Format[Aggregations.Aggregation] {
    def reads(json: JsValue) = JsSuccess(Aggregations.withName(json.as[String]))
    def writes(v: Aggregations.Aggregation) = JsString(v.toString)
  }

  implicit val ActionsFormat = new Format[Actions.Action] {
    def reads(json: JsValue) = JsSuccess(Actions.withName(json.as[String]))
    def writes(v: Actions.Action) = JsString(v.toString)
  }

  implicit val ActionDefFormatter: OFormat[ActionDef] = Json.using[WithDefaultValues].format[ActionDef]
  implicit val RuleFormatter: OFormat[Rule] = Json.using[WithDefaultValues].format[Rule]

}
