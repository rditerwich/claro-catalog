package claro.cms

import java.net.URI
import javax.servlet.{FilterConfig,ServletException}
import net.liftweb.http.LiftFilter
import claro.common.util.Conversions._

class CmsServletFilter extends LiftFilter {

  override def init(config : FilterConfig) = {
    try {
      super.init(new FilterConfig() {
        override def getFilterName = config.getFilterName
        override def getServletContext = config.getServletContext
        override def getInitParameter(name : String) = name match {
          case "bootloader" => classOf[Boot].getName
          case value => value
        }
        override def getInitParameterNames = config.getInitParameterNames
      })
    } catch {
      case e : Exception => throw new ServletException(e)
    }
  }
}