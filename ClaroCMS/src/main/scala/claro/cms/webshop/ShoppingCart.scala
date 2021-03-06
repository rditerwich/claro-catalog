package claro.cms.webshop
import claro.jpa
import claro.cms.{Bindable, Redrawable, CurrentRedraws}
import claro.common.util.Conversions._
import net.liftweb.http.{SessionVar, S, SHtml}
import net.liftweb.http.js.{JsCmd}
import scala.xml.{Node, NodeSeq, Text}
import easyenterprise.lib.util.Money

//TODO correctly calculate promotion prices, currently the original product price is calculated.
//TODO add button "next step"
//TODO on shoppingcart page, when emptying shopping cart, table should be refreshed too.
//FIXME anchor for buttons no are anchor, with # this introduces new history token, or return false or no a, and set style via css, like cursor

object ShoppingCart extends SessionVar[ShoppingCart](new ShoppingCart)

class ShoppingCart private extends Bindable with WebshopBindingHelpers with Redrawable {

	override val defaultPrefix = "shopping-cart"
	
  override lazy val bindings = Map(
  	"items" -> order.productOrders -> "item",
  	"item-count" -> order.productOrders.foldLeft(0)((x,y) => x + y.volume),
    "add" -> addProduct(@@("product-prefix", "product")),
    "add-promotion" -> addPromotion(@@("promotion-prefix", "promotion")),
    "shipping-costs" -> format(order.shippingCosts),
    "total-prices" -> order.totalPrices -> "total-price",
    "total-prices-plus-shipping" -> order.totalPricesPlusShipping -> "total-price",
    "clear" -> clear,
    "link" -> Link("/cart"),
    "also-bought" -> WebshopModel.shop.get.alsoBought(order.productOrders.map(_.product)) -> "product",
    "also-bought2" -> order.productOrders.map(_.product) -> "product",
    "place-order" -> placeOrderLink(@@("href", "/shipping")),
    "proceed-order-link" -> proceedOrderLink(@@("href", "/shipping")))
  
  def order = WebshopModel.currentOrder.get

  def addProduct(product : Product) : NodeSeq => NodeSeq = xml => {
  	val redraws = CurrentRedraws.get
  	def callback(product : Product) = {
  		order.addProduct(product, 1)
  		S.notice("Product added to shopping cart")
  		redraws.toJsCmd
  	}
  	SHtml.a(() => callback(product), xml) % currentAttributes("product-prefix")
  }
  
  def addProduct(productPrefix : String) : NodeSeq => NodeSeq = {
    findBoundObject(productPrefix) match {
      case Some(product : Product) => addProduct(product)
      case _ => xml => NodeSeq.Empty
    }
  }

  def addPromotion(promotion : Promotion) : NodeSeq => NodeSeq = xml => {
    val redraws = CurrentRedraws.get
    def callback(promotion : Promotion) = {
      promotion match {
        case p : VolumeDiscountPromotion =>
          S.notice("Promotion added to shopping cart")
          order.addProduct(p.product, p.volumeDiscount)
        case _ =>
      }
      redraws.toJsCmd
    }
    SHtml.a(() => callback(promotion), xml) % currentAttributes("promotion-prefix")
  }
 
  def addPromotion(promotionPrefix : String) : NodeSeq => NodeSeq = {
    findBoundObject(promotionPrefix) match {
      case Some(promotion:Promotion) => addPromotion(promotion)
      case _ => xml => NodeSeq.Empty
    }
  }  
  def shippingCosts = new Money(15, "EUR")
  
  def proceedOrderLink(href : String) = (xml : NodeSeq) => 
    <a href={href}>{xml}</a> % currentAttributes()
  
  def placeOrderLink(href : String) = (xml : NodeSeq) => {
    val redraws = CurrentRedraws.get
    def callback = {
      WebshopDao.transaction { em =>
        WebshopModel.currentUserVar.get match {
          case Some(user) => WebshopDao.findUserById(user.getId getOrElse(-1)) match {
            case Some(user) => 
              val transport = new jpa.order.Transport
              transport.setDesciption("Standard Delivery")
              transport.setDeliveryTime(14)
              transport.setTransportCompany("UPS")
              
              order.order.setUser(user)
              order.order.setOrderDate(new java.util.Date())
              order.order.setAmountPaid(0d)
              order.order.setStatus(jpa.order.OrderStatus.PendingPayment)
              order.order.setTransport(transport)
              order.order.setShop(WebshopModel.shop.shop)
              em.merge(order.order)
              
              // clear shopping basket
              WebshopModel.currentOrder.remove()
              S.notice("Order has been placed")
              
              // redirect
              if (href.trim != "") {
					      //S.redirectTo(href)
					    }
            case None =>
          }
          case None =>
        }
        redraws.toJsCmd
      }
    }
    SHtml.a(() => callback, xml) % currentAttributes()
  }

  def clear : NodeSeq => NodeSeq = xml => {
    val redraws = CurrentRedraws.get
    def callback = {
      S.notice("Shopping cart cleared")
      order.clear
      redraws.toJsCmd
    }
    SHtml.a(() => callback, xml) % current.attributes
  }

  def updateVolume(productOrder : ProductOrder) = { 
    val redraws = CurrentRedraws.get
    def callback(volume : String) = {
      productOrder.volume = volume.toIntOr(productOrder.volume)
      redraws.toJsCmd
    }
    SHtml.ajaxText(productOrder.volume.toString, callback _) % current.attributes
  }
  
  def removeProductOrder(productOrder : ProductOrder) = (xml : NodeSeq) => {
    val redraws = CurrentRedraws.get
    def callback = {
        productOrder.remove
        redraws.toJsCmd
    }
    SHtml.a(() => callback, xml) % current.attributes
  }
}