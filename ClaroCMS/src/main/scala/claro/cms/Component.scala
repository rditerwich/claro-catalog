package claro.cms

import net.liftweb.http.{LiftRules,LiftResponse}

trait Component extends BindingHelpers {
  val prefix : String
  val bindings = LiftRules.realInstance.RulesSeq[PartialFunction[Any,Map[String,Binding]]]
  val dispatch = LiftRules.realInstance.RulesSeq[PartialFunction[(List[String],String),LiftResponse]]
  val rewrite = LiftRules.realInstance.RulesSeq[Function[List[String],List[String]]]
  val templateLocators = LiftRules.realInstance.RulesSeq[PartialFunction[Template,ResourceLocator]]
  def boot {}
  
  implicit def toList(a : Scope) = List(a)

}
