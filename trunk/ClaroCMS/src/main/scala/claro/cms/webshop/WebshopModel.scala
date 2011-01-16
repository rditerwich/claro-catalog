package claro.cms.webshop

import net.liftweb.http.{ RequestVar, SessionVar, Req, S }
import net.liftweb.common.{ Box, Full }
import java.util.{ Locale, LinkedHashSet }

import scala.xml.NodeSeq
import scala.xml.Text
import scala.collection.{ mutable, immutable, Set }
import scala.collection.JavaConversions._

import claro.jpa
import claro.common.util.{ Delegate, Lazy, KeywordMap, Locales, ProjectionMap }
import claro.common.util.Conversions._
import claro.cms.Website

object WebshopModel {

  var shopCache = Lazy(new WebshopCache)
  object shop extends RequestVar[Shop](shopCache.get.shopsByName(rich(Website.instance.config.properties)("shop.name", "Shop")))

  object currentProductVar extends RequestVar[Option[String]](None)
  object currentCategoryVar extends RequestVar[Option[String]](None)
  object currentSearchStringVar extends RequestVar[Option[String]](None)
  object currentUserVar extends SessionVar[Option[jpa.party.User]](None)
  object currentOrder extends SessionVar[Order](new Order(new jpa.order.Order, shop.mapping))

  def currentProduct: Option[Product] = currentProductVar.is match {
    case Some(id) => Some(shop.productsById(id.toLong))
    case None => None
  }

  def currentCategory: Option[Category] = currentCategoryVar.is match {
    case Some(urlName) => shop.categoriesByUrlName.get(urlName)
    case None => None
  }

  def currentSearchProducts: Set[Product] = currentSearchStringVar.is match {
    case Some(searchString) =>
      val products : Set[Product] = shop.keywordMap.find(searchString)
      currentCategory match {
        case Some(category) => products filter category.productExtent 
        case None => products 
      }

    case _ => Set.empty
  }

  def currentProducts: Seq[Product] = {
    val products = currentSearchStringVar.is match {
      case Some(searchString) =>
        val products = shop.keywordMap.find(searchString)
        currentCategory match {
          case Some(group) => products filter group.productExtent toSeq
          case None => products.toSeq
        }
      case None => currentCategory match {
      	case Some(category) => category.productExtent.toSeq 
      	case None => shop.products.toSeq
      }
    }
    Filtering.filters.foldLeft(products)((x, y) => x filter y.products)
  }

  def isCategorySelected(category: Category): Boolean = {
    currentCategory match {
      case Some(c) => c == category || c.parentExtent.contains(category)
      case None => false
    }
  }

  def flush = {
    shopCache.reset
    shop.remove()
    shop.get
  }
}

class Mapping(product: Option[Product], cacheData: WebshopData) {
  lazy val categories = ProjectionMap((c: jpa.catalog.Category) => new Category(c, c, product, cacheData, this))
  lazy val products = ProjectionMap((p: jpa.catalog.Product) => new Product(p, p, cacheData, this))
  lazy val properties = ProjectionMap((p: jpa.catalog.Property) => new Property(p, noPropertyValue, product, cacheData, this))
  lazy val promotions = ProjectionMap((p: jpa.shop.Promotion) => p match {
    case p: jpa.shop.VolumeDiscountPromotion => new VolumeDiscountPromotion(p, cacheData, this)
    case _ => new Promotion(p, cacheData, this)
  })
}

class Shop(val cacheData: WebshopData) extends Delegate(cacheData.catalog) {

  val mapping = new Mapping(None, cacheData)

  val shop = cacheData.shop
  val catalogId = shop.getCatalog.getId
  val id = shop.getId.getOrElse(-1)
  val serverName: String = (shop.getUrlPrefix getOrElse ("") split ("/"))(0)
  val prefixPath: List[String] = (shop.getUrlPrefix getOrElse ("") split ("/") toList) drop (0)
  val defaultLanguage = shop.getDefaultLanguage getOrElse "en"

  val topLevelCategories: Seq[Seq[Category]] =
    cacheData.topLevelNavigation.map(_.map(n => mapping.categories(n.getCategory)))

  val excludedProperties: Set[Property] =
    cacheData.excludedProperties map (mapping.properties) toSet

  val promotions: Set[Promotion] =
    cacheData.promotions map (mapping.promotions) toSet

  val categories: Set[Category] =
    cacheData.categories map (mapping.categories) toSet

  val categoriesById: collection.Map[Long, Category] =
    categories mapBy (_.id)

  val products: Set[Product] =
    cacheData.products map (mapping.products) toSet

  val productsById: Map[Long, Product] =
    products mapBy (_.id)

  val categoriesByName: Map[String, Category] =
    categories mapBy (_.name)

  val categoriesByUrlName: collection.Map[String, Category] =
    categories mapBy (_.urlName)

  val mediaValues: Map[Long, (String, Array[Byte])] =
    cacheData.mediaValues

  val keywordMap =
    KeywordMap(products map (p => (p.properties map (_.valueAsString), p)))
}

trait Item {
	val item : jpa.catalog.Item
	val cacheData: WebshopData
	val mapping: Mapping
  
	// terminate recursion
	item match {
		case product : jpa.catalog.Product => mapping.products(product) = this.asInstanceOf[Product]
		case category : jpa.catalog.Category => mapping.categories(category) = this.asInstanceOf[Category]
	}
	
	val id = item.getId.longValue
	val parentCategories : Seq[Category] = item.getParents.toSeq.map(_.getParent).classFilter(classOf[jpa.catalog.Category]).map(mapping.categories)
	val parentProducts : Seq[Product] = item.getParents.toSeq.map(_.getParent).classFilter(classOf[jpa.catalog.Product]).map(mapping.products)
	val parents : Seq[Item] = parentCategories ++ parentProducts
	val parentCategoryExtent : Seq[Category] = cacheData.itemParentExtent(item).toSeq.classFilter(classOf[jpa.catalog.Category]).map(mapping.categories)
	val parentProductExtent : Seq[Product] = cacheData.itemParentExtent(item).toSeq.classFilter(classOf[jpa.catalog.Product]).map(mapping.products)
	val parentExtent : Seq[Item] = parentCategoryExtent ++ parentProductExtent
  val childCategories : Seq[Category] = item.getChildren.toSeq.map(_.getChild).classFilter(classOf[jpa.catalog.Category]).map(mapping.categories)
  val childProducts : Seq[Product] = item.getChildren.toSeq.map(_.getChild).classFilter(classOf[jpa.catalog.Product]).map(mapping.products)
  val children : Seq[Item] = childCategories ++ childProducts
  val childCategoryExtent : Seq[Category] = cacheData.itemChildExtent(item).toSeq.classFilter(classOf[jpa.catalog.Category]).map(mapping.categories)
  val childProductExtent : Seq[Product] = cacheData.itemChildExtent(item).toSeq.classFilter(classOf[jpa.catalog.Product]).map(mapping.products)
  val childExtent : Seq[Item] = childCategoryExtent ++ childProductExtent

  val properties: Seq[Property] =
    cacheData.itemPropertyValues(item) map (v =>
      new Property(v.getProperty, v, Some(this), cacheData, mapping))

  val propertyNames: Set[String] =
    properties.map(_.name).toSet

  val propertiesByName: Map[String, Property] =
    properties mapBy (_.name)

  val propertiesByLocaleName: Map[(Locale, String), Property] =
    properties mapBy (p => (p.locale, p.name))
	
	val relatedCategories = Set[Item]()
//		item.getRelations.map(_.getRelatedTo).classFilter(classOf[jpa.catalog.Category]).map(mapping.categories).toSet
	
	val relatedProducts = Set[Product]()
//		item.getRelations.map(_.getRelatedTo).classFilter(classOf[jpa.catalog.Product]).map(mapping.products).toSet
	
	def relatedProducts(categoryName : Option[String]) : Set[Product] = categoryName match {
		case Some(category) => WebshopModel.shop.categoriesByName.get(category) match {
			case Some(category) => relatedProducts.filter(category.childCategoryExtent.toSet)
			case None => Set.empty
		}
		case None => relatedProducts
	}
	
  val name: String =
    propertiesByLocaleName.get(Locales.empty, "Name") match {
      case Some(property) => property.value.getStringValue
      case None => ""
    }


  val urlName: String = name.replace(" ", "").toLowerCase

  def properties(locale: Locale) =
    propertyNames.map(property(locale, _))

  def property(locale: Locale, name: String): Option[Property] = {
    for (alt <- Locales.getAlternatives(locale)) {
      propertiesByLocaleName.get(alt, name) match {
        case Some(property) => return Some(property)
        case _ =>
      }
    }
    None
  }
  override def toString = name
}

class Category(category: jpa.catalog.Category, val item : jpa.catalog.Item, val productqwer: Option[Product], val cacheData: WebshopData, val mapping: Mapping) extends Delegate(category) with Item {
	
  val products = childProducts.toSet
  val productExtent = childProductExtent.toSet

//  val groupProperties: Seq[Property] =
//    cacheData.categoryPropertyValues(category) map (v =>
//      new Property(v.getProperty, v, None, cacheData, mapping))
//
//  val groupPropertyNames: Set[String] =
//    groupProperties.map(_.name).toSet
//
//  val groupPropertiesByLocaleName: Map[(Locale, String), Property] =
//    groupProperties mapBy (p => (p.locale, p.name))
//
//  def groupProperties(locale: Locale) =
//    groupPropertyNames.map(groupProperty(locale, _))
//
//  def groupProperty(locale: Locale, name: String): Option[Property] = {
//    for (alt <- Locales.getAlternatives(locale)) {
//      groupPropertiesByLocaleName.get(alt, name) match {
//        case Some(property) => return Some(property)
//        case _ =>
//      }
//    }
//    None
//  }

  lazy val productExtentPromotions: Set[Promotion] = {
    val promotions = cacheData.promotions map (mapping.promotions) filter (p => !(p.products ** productExtent).isEmpty)
    if (promotions isEmpty) Set.empty else Set(promotions.toSeq first)
  }
}

class Product(product: jpa.catalog.Product, val item : jpa.catalog.Item, val cacheData: WebshopData, val mapping: Mapping) extends Delegate(product) with Item {

  val categories = parentCategories.toSet
  val categoryExtent = parentCategoryExtent.toSet
  
  val priceProperty: Option[Property] = property(Locales.empty, "Price")
}

class Property(property: jpa.catalog.Property, val value: jpa.catalog.PropertyValue, val item: Option[Item], cacheData: WebshopData, mapping: Mapping) extends Delegate(property) {
  // terminate recursion
  mapping.properties(property) = this

  val id: Long = property.getId.longValue
  val propertyType: jpa.catalog.PropertyType = property.getType

  val namesByLanguage: Map[Option[String], String] =
    Map(property.getLabels.toSeq map (l => (l.getLanguage asOption, l.getLabel)): _*)

  val name: String = namesByLanguage.get(None) getOrElse ""

  val valueId: Long = value.getId.longValue
  val mimeType: String = value.getMimeType getOrElse ""
  val mediaValue: Array[Byte] = value.getMediaValue
  val moneyValue: Double = value.getMoneyValue.getOrElse(0)
  val moneyCurrency: String = value.getMoneyCurrency

  val locale = Locales(value.getLanguage)

  def hasValue = value != noPropertyValue

  //FIXME: check should not be on null check but on property type
  val valueAsString =
    if (value.getStringValue != null) value.getStringValue
    else if (value.getBooleanValue != null) value.getBooleanValue.toString
    else if (value.getEnumValue != null) value.getEnumValue.toString
    else if (value.getIntegerValue != null) value.getIntegerValue.toString
    else if (value.getMoneyValue != null) "&euro; " + value.getMoneyValue.toString
    else if (value.getRealValue != null) value.getRealValue.toString
    else ""

  override def toString = name
}

object groupByLocale {

  def apply(properties: Seq[Property]): Map[Locale, Seq[Property]] = {
    val loc_prop: Set[(Locale, Property)] = Set(properties.map(p => Locales(p.value.getLanguage getOrElse ("")) -> p): _*)
    val all_loc_prop = new mutable.ArrayBuffer[(Locale, Property)]
    for ((locale, property) <- loc_prop) {
      all_loc_prop += ((locale, property))
      var alt = Locales.getAlternatives(locale).tail
      while (alt != Nil && !loc_prop.contains((alt.head, property))) {
        all_loc_prop += ((alt.head, property))
        alt = alt.tail
      }
    }
    all_loc_prop.map(_._2).groupBy(p => Locales(p.value.getLanguage)).toMap
  }
}

object noPropertyValue extends jpa.catalog.PropertyValue {
  setId(-1l)
}

case class Money(amount: Double, currency: String) {}

class Promotion(promotion: jpa.shop.Promotion, cacheData: WebshopData, mapping: Mapping) extends Delegate(promotion) {
  // terminate recursion
  mapping.promotions(promotion) = this
  val id = promotion.getId.longValue
  def products: Set[Product] = Set.empty
}

class VolumeDiscountPromotion(promotion: jpa.shop.VolumeDiscountPromotion, cacheData: WebshopData, mapping: Mapping) extends Promotion(promotion, cacheData, mapping) {
  val startDate = promotion.getStartDate
  val endDate = promotion.getEndDate
  val price = promotion.getPrice.getOrElse(0)
  val priceCurrency = promotion.getPriceCurrency
  val volumeDiscount = promotion.getVolumeDiscount.getOrElse(0)
  val product = mapping.products(promotion.getProduct)
  override def products = Set(product)
}

//FIXME calculate promotion price when calculating new price

class Order(val order: jpa.order.Order, mapping: Mapping) extends Delegate(order) {

  def productOrders: Seq[ProductOrder] =
    order.getProductOrders map (new ProductOrder(_, this, mapping)) toSeq

  def clear = delegate.getProductOrders.clear

  def isEmpty = delegate.getProductOrders == null || delegate.getProductOrders.isEmpty

  /**
   * Calculates the total number of articles in the shopping cart, based on
   * volume. 
   */
  def totalProducts: Int = {
    (0 /: delegate.getProductOrders.map(_.getVolume.intValue))(_ + _)
  }

  def currencies = productOrders map (_.currency) toSet

  def totalPrice(currency: String): Double = {
    (0.0 /: productOrders.filter(_.currency == currency).map(_.totalPrice))(_ + _)
  }

  def totalPrices: Seq[Money] = currencies.toSeq map (c => Money(totalPrice(c), c))

  var shippingCosts = Money(15, "EUR")

  def totalPricesPlusShipping: Seq[Money] = totalPrices.map(m => if (m.currency == shippingCosts.currency) Money(m.amount + shippingCosts.amount, m.currency) else m)

  /**
   * Adds a product to the order list. If the product already is present update
   * the volume count for that product.
   */
  def addProduct(product: Product, volume: Int) = {
    productOrders.find(_.product.id == product.id) match {
      case Some(productOrder) => productOrder.volume += volume
      case None =>
        val productOrder = new jpa.order.ProductOrder
        productOrder.setProduct(product.delegate)
        productOrder.setVolume(volume)
        product.priceProperty match {
          case Some(property) =>
            productOrder.setPrice(property.moneyValue)
            // TODO what is the default currency?
            productOrder.setPriceCurrency(property.moneyCurrency getOrElse "euro")
          case None =>
        }
        productOrder.setOrder(delegate)
        delegate.getProductOrders.add(productOrder)
    }
  }
}

class ProductOrder(val productOrder: jpa.order.ProductOrder, val order: Order, mapping: Mapping) extends Delegate(productOrder) {
  val product = mapping.products(productOrder.getProduct)
  def price = productOrder.getPrice.getOrElse(0)
  def totalPrice = price * volume
  def currency = productOrder.getPriceCurrency
  def volume = productOrder.getVolume.getOrElse(0)
  def volume_=(v: Int) = productOrder.setVolume(v)

  def remove = {

  	val result = order.delegate.getProductOrders.filter(_.getProduct.getId != productOrder.getProduct.getId)
  	order.delegate.setProductOrders(new java.util.ArrayList[jpa.order.ProductOrder](result))
  }
}

case class ShippingOption(description: String, price: Money)

