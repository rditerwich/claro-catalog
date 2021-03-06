package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import xml.{Node, NodeSeq, MetaData}
import claro.jpa
import claro.cms.{Form,Mail,Website}
import easyenterprise.lib.util.Money
object ShippingOptionsForm extends RequestVar[ShippingOptionsForm](new ShippingOptionsForm) 

class SelectedShippingOption(var checked : Boolean, val option : ShippingOption) {
  def field = SHtml.checkbox(checked, x => checked = x, ("type", "radio"), "name" -> "g1")
}

class ShippingOptionsForm extends Form {
	
  def order = WebshopModel.currentOrder.get

  def shippingOptions = List(
    new SelectedShippingOption(false, ShippingOption("Normal delivery", new Money(15, "EUR"))),
    new SelectedShippingOption(false, ShippingOption("Express delivery", new Money(45, "EUR")))
  )
  
  var shippingOption : Option[ShippingOption] = None
  
  val submitButton = Submit("proceed") {
  	println(order.delegate.getDeliveryAddress.getAddress1)
  	println(order.delegate.getDeliveryAddress.getAddress1)
  	
  }

  if (order.delegate.getDeliveryAddress == null) {
    val address = new jpa.party.Address
    order.delegate.setDeliveryAddress(address)
    
    // copy delivery address from user
    WebshopModel.currentUserVar.get match {
      case Some(user) if (user.getParty.getDeliveryAddress != null) =>
        address.setAddress1(user.getParty.getDeliveryAddress.getAddress1)
        address.setAddress2(user.getParty.getDeliveryAddress.getAddress2)
        address.setTown(user.getParty.getDeliveryAddress.getTown)
        address.setPostalCode(user.getParty.getDeliveryAddress.getPostalCode)
        address.setCountry(user.getParty.getDeliveryAddress.getCountry)
      case _ =>
    }
  }
    
  val deliveryAddressForm = Nested(new AddressForm(order.delegate.getDeliveryAddress))
}