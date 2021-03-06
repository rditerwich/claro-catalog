package claro.cms

import xml.{Node,NodeSeq}
import net.liftweb.http.{RequestVar}
import net.liftweb.http.js.{JsCmd,JsCmds}
import collection.mutable

/**
 * Redrawing
 */

object CurrentRedraws extends RequestVar[Redraws](new Redraws)

class Redraws extends mutable.HashMap[String,() => NodeSeq] {
  def toJsCmd = {
    CurrentRedraws.set(this)
    this.map(r => new JsCmds.SetHtml(r._1, r._2())).foldLeft(JsCmds.Noop)(_ & _)
  }
}
  
trait Redrawable extends Binding {

  abstract override def bind(node : Node, context : BindingContext) : NodeSeq = {
    val redraws = CurrentRedraws.get
    val id = "implicit" + redraws.size
    redraws += (id, () => super.bind(node, context))
    <span id={id} class="implicit">
      { super.bind(node, context) }
    </span>
  }
}