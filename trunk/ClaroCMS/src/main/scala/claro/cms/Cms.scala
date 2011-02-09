package claro.cms

import net.liftweb.http.{Req,LiftRules,LiftSession,LiftResponse,RulesSeq,RequestVar,S,InMemoryResponse,OkResponse,XhtmlResponse,CSSResponse}
import net.liftweb.common.{Box,Full,Empty,Logger}
import xml.Node
import java.util.Locale
import javax.persistence.{EntityManager,EntityManagerFactory}
import javax.servlet.http.HttpServletRequest
import claro.common.util.Locales
import claro.common.util.Conversions._

object Cms {

  val components = RulesSeq[() => Component]
  val logger = Logger("CMS")

  val debugMode : Boolean = System.getProperties().isSet("claro.cms.debug")
  
  def caching = !debugMode

  components.append(() => new TemplateComponent)
  components.append(() => new claro.cms.components.StdComponent)
  components.append(() => new claro.cms.components.Utils)
  components.append(() => new claro.cms.components.MenuComponent)

  object locale extends RequestVar[Locale](Website.instance.defaultLocale)
  
  def previewMode = S.request match {
  	case Full(request) => request.hostName.startsWith("preview-") || request.hostName.startsWith("preview.")
  	case _ => false
  }
  
  if (debugMode) {
  	println("Claro CMS started in DEBUG mode")
  }
}
