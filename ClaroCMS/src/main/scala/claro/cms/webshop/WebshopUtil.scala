package claro.cms.webshop

import java.util.Date
import java.text.SimpleDateFormat
import net.liftweb.http.js.{JsCmd,JsCmds}
import net.liftweb.common.{ Box, Full, Empty }
import net.liftweb.util.{ Bindable }
import net.liftweb.http.{ S, SHtml }
import scala.xml.{ NodeSeq, Node, Text, Unparsed, Elem, UnprefixedAttribute, TopScope }
import claro.cms.BindingHelpers
import claro.jpa

object WebshopUtil {

  val noSlashDate = new SimpleDateFormat("yyyyMMdd")
  val slashDate = new SimpleDateFormat("yyyy/MM/dd")

  def splitEvery[A](as: List[A], n: Int): List[List[A]] = as.splitAt(n) match {
    case (a, Nil) => a :: Nil
    case (a, b) => a :: splitEvery(b, n)
  }

  def getIntParam(name: String, default: Int): Int = {
    try {
      S.param(name).map(_.toInt) openOr default
    } catch {
      case e => default // Should log something in this case
    }
  }

  type DateConverter = String => Date

  def parseDate(value: String, converter: DateConverter): Box[Date] =
    try {
      Full(converter(value))
    } catch {
      case e => Empty
    }

  def getDateParam(name: String, converter: DateConverter): Box[Date] = {
    S.param(name).map(parseDate(_, converter)) openOr Empty
  }
}

object Link extends BindingHelpers {
	def home = (xml: NodeSeq) => <a href="/">{xml}</a>
  def apply(category: Category) = (xml: NodeSeq) => {
    val href = "/category/" + category.urlName + (WebshopModel.currentSearchStringVar.get match {
      case Some(s) if (attr(current, "include-search-result", "false") == "true") => "/search/" + s
      case _ => ""
    })
    new Elem(null, "a", new UnprefixedAttribute("href", href, current.attributes), TopScope, xml: _*);
  }

  def apply(product: Product) = (xml: NodeSeq) =>
    new Elem(null, "a", new UnprefixedAttribute("href", "/product/" + product.id, current.attributes), TopScope, xml: _*);

  def apply(path: String) = (xml: NodeSeq) => <a href={ path }>{ xml }</a> % currentAttributes()
}

object ParentLink extends BindingHelpers {
  def apply(item : Item) = (xml: NodeSeq) => {
  	Trail.parentOnTrail(item) match {
	  	case Some(category : Category) => Link(category)(xml)
	  	case Some(product : Product) => Link(product)(xml)
	  	case _ => Link("/")(xml)
  	}
  }
}

object LinkAttr {
  def apply(category: Category) = new LinkAttr(Text("/category/" + category.urlName))
  def apply(product: Product) = new LinkAttr(Text("/product/" + product.id))
  def apply(path: String) = new LinkAttr(Text(path))
}

class LinkAttr(val value: NodeSeq) {}
