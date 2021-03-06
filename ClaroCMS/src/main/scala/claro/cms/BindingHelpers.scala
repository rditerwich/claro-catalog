package claro.cms

import xml.{ Elem, Node, Text }
import claro.cms.util.Formatting

object BindingHelpers extends BindingHelpers

trait BindingHelpers extends BindingCtor {
	
  def locale = Cms.locale
 
  def website = Website.instance 
  
  def current : Elem = BindingContext.current.get._2
  
  def currentContext : BindingContext = BindingContext.current.get._1

  def currentAttributes(excl : String*) = 
    current.attributes filter(a => !excl.contains(a.key))

  def @@(name : String) : String = attr(current, name)
  
  def @@(name : String, default : => Any) : String = attr(current, name, default) 
  
  def @@?(name : String) : Option[String] = attrOption(current, name)
  		
  def @@?[A](name : String, yes : => A, no : => A) = ifAttr(name, yes, no)
  
  def attr(node : Node, name : String) : String = attr(node, name, throw new Exception("Missing attribute: " + name))
    
  def attrOption(node : Node, name : String) : Option[String] = 
  	node.attributes.find(at => at.key == name && !at.isPrefixed) match {
  	case Some(attr) => Some(attr.value.toString)
  	case None => None
  }
  
  def attr(node : Node, name : String, default : => Any) : String = attrOption(node, name) match {
  	case Some(value) => value
  	case none => default.toString
  }

  def attr[A](node : Node, name : String, map : String => A, default : => A) : A = 
	  node.attributes.find(at => at.key == name && !at.isPrefixed) match {
	  case Some(attr) => map(attr.value.toString)
	  case None => default
  }
  
  def ifAttr[A](name : String, yes : => A, no : => A) = if (attr(current, name, "false") == "true") yes else no
 
  def bindingsFor(obj : Any) = obj match {
    case null => Map[String,Binding]()
    case obj => Website.instance.rootBinding.cache(obj)
  }
  
  def findBoundObject(prefix : String) = currentContext.bindings.get(prefix) match {
    case Some(Bindings(obj, _)) => Some(obj)
    case None => None
  }
  
  def formatMoney(amount : Double, currency : String) = ifAttr("plain", Text(Formatting.formatMoneyText(amount, currency)), Formatting.formatMoneyHtml(amount, currency))
}
