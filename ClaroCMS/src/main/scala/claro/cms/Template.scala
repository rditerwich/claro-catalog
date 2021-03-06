package claro.cms

import scala.xml.{Node,NodeSeq,XML,Text}
import scala.xml.NodeSeq._
import scala.collection.{mutable}
import scala.collection.JavaConversions._
import java.io.{File,FileInputStream}
import java.util.Locale
import java.util.concurrent.{ConcurrentHashMap,TimeUnit}
import claro.cms.util.ParseHtml
import claro.common.util.Locales
import claro.common.util.Conversions._

object Template {
  def apply(name : String) = new Template(List(name), null)
  def apply(path : List[String]) = new Template(path, null)
}

case class Template(path : List[String], obj : Any) {
}

case class ConcreteTemplate(resource : Resource, xml : NodeSeq) {

  private def tuples : Seq[(String,NodeSeq)] = xml filter (isNested(_)) map (node => (name(node), NodeSeq.fromSeq(node.child)))
  private def tuples2 : Seq[(String,NodeSeq)] = tuples ++ Seq(("", contents))
  val nestedTemplates = Map(tuples2:_*)  

  private def contents = xml filter (!isNested(_))
  private def name(node : Node) : String = node.attribute("template") getOrElse Seq.empty toString
  private def isNested(node : Node) = node.prefix == "template" && node.label == "define"
}

class TemplateCache(val store : TemplateStore) {
  private val objectTemplateCache = new ConcurrentHashMap[(Template,Locale),Option[ConcreteTemplate]]()

  def apply(template : String, locale : Locale) : Option[ConcreteTemplate] = apply(Template(template), locale)
    
  def apply(template : Template, locale : Locale) : Option[ConcreteTemplate] =
    if (!Cms.caching) store.find(template, locale) 
    else objectTemplateCache getOrElseUpdate ((template,locale), store.find(template, locale))

  private[cms] def flush(templatePath : List[String]) = {
    val it = objectTemplateCache.keySet.iterator
    while (it.hasNext) {
      if (it.next._1.path == templatePath) {
        it.remove
      }
    }
  }
}

class TemplateStore(templateLocators : Seq[PartialFunction[Template,ResourceLocator]], resourceStore : ResourceStore) {

  def find(template : Template, locale : Locale) : Option[ConcreteTemplate] = 
    resourceStore.find(resourceLocator(template), Locales.getAlternatives(locale)) match {
      case Some(resource) => 
        ParseHtml(resource.readStream, resource.path.mkString("/")) match {
          case (xml, None) => Some(ConcreteTemplate(resource, xml))
          case (xml, Some(e)) => Some(ConcreteTemplate(resource, <html>{xml}</html>))
        }
      case None => None
    }
  
  private def resourceLocator(template : Template) : ResourceLocator =
    templateLocators.findFirst(template).
      getOrElse (ResourceLocator(template.path, "html", List(Scope.global)))
}

class TemplateComponent extends Component {
  
  val prefix = "template"
  
  bindings.append {
    case _ : TemplateComponent => Map(
      "include" -> new IncludeBinding(Map.empty))
  }

  class IncludeBinding(currentTemplates : Map[String,NodeSeq]) extends Binding {
    def bind(node : Node, context : BindingContext) : NodeSeq = {
      val name = @@("template")
      val (templateNodes,contentNodes) = node.child.partition(node => node.prefix == prefix && node.label == "define")
      val templateMap : Map[String,NodeSeq] = Map(templateNodes.toSeq map(n => (attr(n, "template"), NodeSeq.fromSeq(n.child))):_*)
      val content : NodeSeq = contentNodes.toSeq
      val template : NodeSeq = currentTemplates.get(name) getOrElse (website.templateCache(name, locale) match {
        case Some(template) => template.xml
        case None => content
      })
		  Binding.bind(template, context + (prefix -> Bindings(Some(this), Map(
	        "include" -> new IncludeBinding(currentTemplates ++ templateMap),
	        "content" -> Binding.bind(content, context)))))
	    }
  }
}

