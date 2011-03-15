package claro.cms.webshop
import claro.jpa
import claro.catalog.CatalogDao
import claro.catalog.data.{PropertyGroupInfo, MediaValue}
import claro.catalog.model.{PropertyModel, ItemModel, MediaContentCache, CatalogModel}
import claro.cms.Cms
import claro.common.util.{Delegate, KeywordMap, Locales, ProjectionMap}
import claro.common.util.Conversions._
import easyenterprise.lib.util.Money
import java.util.Locale
import net.liftweb.http.{RequestVar, SessionVar, LiftRules}
import net.liftweb.http.provider.servlet.HTTPServletContext
import scala.collection.{mutable, immutable, Set}
import scala.collection.JavaConversions._
import scala.util.Random

object WebshopModel {

	lazy val dao = new CatalogDao(LiftRules.context match {
    case context: HTTPServletContext => Map(context.initParams:_*)
    case _ => Map[String,String]()
  })
	
	lazy val mediaContentCache = new MediaContentCache(dao)
	
  object shop extends RequestVar[Shop](WebshopCache(Cms.previewMode, Cms.locale.getISO3Language))

  object currentProductVar extends RequestVar[Option[String]](None)
  object currentCategoryVar extends RequestVar[Option[String]](None)
  object currentSearchStringVar extends RequestVar[Option[String]](None)
  object currentUserVar extends SessionVar[Option[jpa.party.User]](None)
  object currentOrder extends SessionVar[Order](new Order(new jpa.order.Order, shop.cacheData, shop.mapping))

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
  
  def mediaContent(mediaContentId : Long) : Option[(String, Array[Byte])] = 
  	mediaContentCache.getMediaContent(new java.lang.Long(mediaContentId)) match {
	  	case mediaContent if mediaContent != null && mediaContent.getData != null => Some((mediaContent.getMimeType, mediaContent.getData))
	  	case _ => None
	  }
}

class Mapping(product: Option[Product], cacheData: WebshopData) {
  lazy val categories = ProjectionMap((item : ItemModel) => new Category(item, cacheData, this))
  lazy val products = ProjectionMap((item : ItemModel) => new Product(item, cacheData, this))
  lazy val propertyGroups = ProjectionMap((propertyGroup : PropertyGroupInfo) => new PropertyGroup(propertyGroup, cacheData, this))
  lazy val properties = ProjectionMap((property : PropertyModel) => new Property(property, cacheData, this))
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
  	cacheData.topLevelNavigation.map(_.map(n => mapping.categories(cacheData.item(n.getCategory))))

  val promotions: Set[Promotion] =
    cacheData.promotions map (mapping.promotions) toSet

  def randomPromotions(count : Int) = 
  	Random.shuffle(promotions).take(if (count < 0 || count > promotions.size) promotions.size else count)
    
  val categories: Set[Category] =
    cacheData.items.filter(_.isCategory) map (mapping.categories) toSet

  val products: Set[Product] =
  	cacheData.items.filter(_.isProduct) map (mapping.products) toSet
    
  val categoriesById: collection.Map[Long, Category] =
    categories mapBy (_.id)

  val productsById: Map[Long, Product] =
    products mapBy (_.id)

  val categoriesByName: Map[String, Category] =
    categories mapBy (_.name)

  val categoriesByUrlName: collection.Map[String, Category] =
    categories mapBy (_.urlName)

  val keywordMap =
    KeywordMap(products map (p => (p.properties map (_.valueAsString), p)))
    
  val alsoBought : Map[Product, Seq[Product]] = 
  	cacheData.alsoBought.map(kv => (mapping.products(kv._1), kv._2.map(mapping.products)))
  	
  def alsoBought(products : Seq[Product]) : Seq[Product] = {
  	val result = mutable.LinkedHashSet[Product]()
  	result ++= products.flatMap(p => alsoBought.getOrElse(p, Seq()))
  	result --= products
  	result.toSeq
  }
}

trait Item {
	val item : ItemModel
	val cacheData: WebshopData
	val mapping: Mapping
  
	val isCategory = item.isCategory
	val isProduct = item.isProduct
	
	// terminate recursion
	if (isCategory) mapping.categories(item) = this.asInstanceOf[Category]
	if (isProduct) mapping.products(item) = this.asInstanceOf[Product]
	
	val id = item.getItemId.longValue
	val parentCategories : Seq[Category] = item.getParents.toSeq.filter(_.isCategory).map(mapping.categories)
	val parentProducts : Seq[Product] = item.getParents.toSeq.filter(_.isProduct).map(mapping.products)
	val parents : Seq[Item] = parentCategories ++ parentProducts
	val parentCategoryExtent : Seq[Category] = item.getParentExtent.toSeq.filter(_.isCategory).map(mapping.categories)
	val parentProductExtent : Seq[Product] = item.getParentExtent.toSeq.filter(_.isProduct).map(mapping.products)
	val parentExtent : Seq[Item] = parentCategoryExtent ++ parentProductExtent
	val childCategories : Seq[Category] = item.getChildren.toSeq.filter(_.isCategory).map(mapping.categories)
	val childProducts : Seq[Product] = item.getChildren.toSeq.filter(_.isProduct).map(mapping.products)
	val children : Seq[Item] = childCategories ++ childProducts
	val childCategoryExtent : Seq[Category] = item.getChildExtent.toSeq.filter(_.isCategory).map(mapping.categories)
	val childProductExtent : Seq[Product] = item.getChildExtent.toSeq.filter(_.isProduct).map(mapping.products)
	val childExtent : Seq[Item] = childCategoryExtent ++ childProductExtent

  val properties: Seq[Property] =
  	item.getPropertyExtent.toSeq.map(_.getValue).filterNot(cacheData.isExcluded(_)) map (mapping.properties)
  	
	val propertiesByGroup: Seq[PropertiesByGroup] =
		for (key <- item.getPropertyExtent.getKeys.toSeq) 
			yield PropertiesByGroup(
					mapping.propertyGroups(key),  
					item.getPropertyExtent.getAll(key).toSeq.filterNot(cacheData.isExcluded(_)) map (mapping.properties))

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
	
	def nameProperty = item.findProperty(item.getCatalog.nameProperty.getPropertyId, true)
	
  val name: String = nameProperty match {
		case null => ""
		case property => mapping.properties(property).valueAsString
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

class Category(val item : ItemModel, val cacheData: WebshopData, val mapping: Mapping) extends Delegate(item) with Item {
	
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
    if (promotions isEmpty) Set.empty else promotions.toSet
  }
  
  def randomProductExtentPromotions(count : Int) = 
  	Random.shuffle(productExtentPromotions).take(if (count < 0 || count > productExtentPromotions.size) productExtentPromotions.size else count)

}

class Product(val item : ItemModel, val cacheData: WebshopData, val mapping: Mapping) extends Delegate(item) with Item {

  val categories = parentCategories.toSet
  val categoryExtent = parentCategoryExtent.toSet
  
  val priceProperty: Option[Property] = property(Locales.empty, "Price")
}

class PropertyGroup(propertyGroup: PropertyGroupInfo, cacheData: WebshopData, mapping: Mapping) extends Delegate(propertyGroup) {

	val namesByLanguage : Map[Option[String], String] = 
		Map(propertyGroup.labels.toSeq map (l => (l.getKey asOption, l.getValue)):_*)
	
	val name : String = propertyGroup.labels.tryGet(cacheData.language, null)
}

case class PropertiesByGroup(propertyGroup : PropertyGroup, properties : Seq[Property])

class Property(property: PropertyModel, cacheData: WebshopData, mapping: Mapping) extends Delegate(property) {

	// terminate recursion
  mapping.properties(property) = this

  val info = property.getPropertyInfo
  val id: Long = property.getPropertyId.longValue
  val propertyType: jpa.catalog.PropertyType = info.getType

  val namesByLanguage: Map[Option[String], String] =
    Map(info.labels.toSeq map (l => (l.getKey asOption, l.getValue)): _*)

  val name : String = info.labels.tryGet(cacheData.language, null)
  val value : Any = property.getEffectiveValues(cacheData.staging, cacheData.shop).tryGet(cacheData.language, null)

  val hasMedia = value match {
  	case media : MediaValue => !media.isEmpty
  	case _ => false
  }
  
  val mediaContentId : Long = value match {
  	case media : MediaValue => media.mediaContentId.getOrElse(-1)
  	case _ => -1
  }
  
  val mimeType : String = value match {
	  case media : MediaValue => media.mimeType
	  case _ => ""
	}
  lazy val mediaContent : Array[Byte] = value match {
	  case media : MediaValue if !media.isEmpty => WebshopModel.mediaContent(media.mediaContentId.longValue) match {
	  	case Some((_, data)) => data
	  	case None => Array()
	  }
	  case _ => Array()
	}
  
  val mediaName : String = value match {
	  case media : MediaValue => media.name
	  case _ => ""
	}
  val moneyValue : Double = value match {
	  case money : Money => if (money.value != null) money.value.doubleValue else 0
	  case _ => 0
	}
  val moneyCurrency : String = value match {
	  case money : Money => money.currency
	  case _ => ""
	}

  val locale = Locales(cacheData.language)

  def hasValue = value != noPropertyValue

  //FIXME: check should not be on null check but on property type
  val valueAsString = if (value == null) "" else propertyType match {
  	case jpa.catalog.PropertyType.String => value.toString 
  	case jpa.catalog.PropertyType.Boolean => if (value == java.lang.Boolean.TRUE) "yes" else "false"
  	case jpa.catalog.PropertyType.Enum => value.toString 
  	case jpa.catalog.PropertyType.Money => moneyValue + " " + moneyCurrency
  	case jpa.catalog.PropertyType.Real => value.toString
  	case _ => value.toString 
  }

  override def toString = name
}

//object groupByLocale {
//
//  def apply(properties: Seq[Property]): Map[Locale, Seq[Property]] = {
//    val loc_prop: Set[(Locale, Property)] = Set(properties.map(p => Locales(p.value.getLanguage getOrElse ("")) -> p): _*)
//    val all_loc_prop = new mutable.ArrayBuffer[(Locale, Property)]
//    for ((locale, property) <- loc_prop) {
//      all_loc_prop += ((locale, property))
//      var alt = Locales.getAlternatives(locale).tail
//      while (alt != Nil && !loc_prop.contains((alt.head, property))) {
//        all_loc_prop += ((alt.head, property))
//        alt = alt.tail
//      }
//    }
//    all_loc_prop.map(_._2).groupBy(p => Locales(p.value.getLanguage)).toMap
//  }
//}

object noPropertyValue extends jpa.catalog.PropertyValue {
  setId(-1l)
}

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
  val product = mapping.products(cacheData.item(promotion.getProduct))
  override def products = Set(product)
}

//FIXME calculate promotion price when calculating new price

class Order(val order: jpa.order.Order, cacheData: WebshopData, mapping: Mapping) extends Delegate(order) {

  def productOrders: Seq[ProductOrder] =
    order.getProductOrders map (new ProductOrder(_, this, cacheData, mapping)) toSeq

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

  def totalPrices: Seq[Money] = currencies.toSeq map (c => new Money(totalPrice(c), c))

  var shippingCosts = new Money(15, "EUR")

  def totalPricesPlusShipping: Seq[Money] = totalPrices.map(m => if (m.currency == shippingCosts.currency) new Money(m.value.doubleValue + shippingCosts.value.doubleValue, m.currency) else m)

  /**
   * Adds a product to the order list. If the product already is present update
   * the volume count for that product.
   */
  def addProduct(product: Product, volume: Int) = {
    productOrders.find(_.product.id == product.id) match {
      case Some(productOrder) => productOrder.volume += volume
      case None =>
        val productOrder = new jpa.order.ProductOrder
        productOrder.setProduct(product.delegate.getEntity.asInstanceOf[jpa.catalog.Product])
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

class ProductOrder(val productOrder: jpa.order.ProductOrder, val order: Order, cacheData: WebshopData, mapping: Mapping) extends Delegate(productOrder) {
  val product = mapping.products(cacheData.item(productOrder.getProduct))
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

