package claro.scalaweb

abstract class Bindable[A](objectType : Class[A]) {
	val defaultPrefix : String
	val bindings : Set[Binding]
	val documentation : String
	
	def bind(label : String, doc : String) = new BindingCtor[A](label)
	implicit def ctor(label : String) = new BindingCtor[A](label)
	implicit def toSet(binding : Binding) = Set(binding)
	implicit def toSet(binding : TextBinding[A]) = Set(binding)
	def @@?(label : String, default : Boolean, documentation : String = "") = this
}

class BindableMap {
	def apply[A](bindableType : Class[A]) : Bindable[A] = null
}

class BindingCtor[A](label : String) {
	def apply(f : => Binding) = f
	def attr(label : String, default : Boolean, doc : String) = false
}

case class Person(firstName: String, lastName : String)
//
//object PersonBindable extends Bindable(classOf[Person]) {
//	val prefix = "person"
//	val documentation = ""
//
//			
////	"first-name" text(_.firstName) @@("Outputs the first name of the person")
//		
//}