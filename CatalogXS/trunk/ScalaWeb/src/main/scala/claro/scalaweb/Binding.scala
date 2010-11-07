package claro.scalaweb

import collection.mutable
import xml.{Elem, NodeSeq}

trait Binding {
	val label : String
	val attrs : Set[Binding] = Set.empty
	val elems : Set[BindingElem] = Set.empty
	val children : Set[Binding] = Set.empty
	val doc : Option[String] = None
	
	def preprocess(xml : NodeSeq) : NodeSeq = xml
}	

trait BindingAttr {
	val label : String
	val doc : Option[String] = None
}

trait BindingElem {
	val prefix : Option[String] = None
	val label : String
	val doc : Option[String] = None
}

trait BindingHelpers {
	def elem(prefix_ : String, label_ : String) = new BindingElem {
		override val prefix = Some(prefix_)
		val label = label_
	}
	def elem(label_ : String) = new BindingElem {
		val label = label_
	}
}

class AnyBinding[A](val label : String, f : => Option[A]) extends Binding with BindingHelpers {
	val elseElem = elem("else")
	override val elems = Set[BindingElem](elseElem) 
}

class TextBinding[A](override val label : String, f : => Option[String]) extends AnyBinding[String](label, f) {
}

object test2 {
	val xml = 
		<root>
			<webshop:all-products prefix="product">
				<div>
      	  <product:name/>
				</div>
			</webshop:all-products>
		</root>
}

