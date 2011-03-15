package claro.cms.webshop

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.xml.NodeSeq 
import java.util.Locale
import java.util.concurrent.{ConcurrentMap, TimeUnit}
import claro.catalog.data.MediaValue
import claro.catalog.model.CatalogModel
import claro.catalog.command.items.PerformStaging
import claro.catalog.model.ItemModel
import claro.catalog.model.PropertyModel

import claro.jpa
import claro.cms.Cms
import claro.cms.Website
import claro.common.util.MapMaker
import claro.common.util.Conversions._

object WebshopCache {
	
	def apply(previewMode : Boolean, language : String) = map((previewMode, language))

	val shopName = rich(Website.instance.config.properties)("shop.name", "Shop")
	val catalogName = rich(Website.instance.config.properties)("catalog.name", "Catalog")
	lazy val catalog = findAllCatalogs.find(_.getName == catalogName).get
	lazy val catalogModel = new CatalogModel(catalog.getId, WebshopModel.dao)
	
  private val map : ConcurrentMap[(Boolean, String), Shop] = MapMaker(10, MapMaker.STRONG, MapMaker.SOFT, key => {
  	val stagingAreaName = if (key._1) PerformStaging.STAGING_AREA_PREVIEW else PerformStaging.STAGING_AREA_PUBLISHED
  	val stagingArea = WebshopModel.dao.getOrCreateStagingArea(stagingAreaName)
  	val language = key._2
  	println("SHOPS:" + findAllShops.map(_.getName).mkString(","))
  	println("FINDING SHOP:" + shopName)
  	val shop = findAllShops.find(_.getName == shopName).get
  	println("SHOP FOUND:" + shop)
  	val shopModel = new Shop(new WebshopData(catalogModel, shop, stagingArea, language))
  	// save memory: catalogModel is mainly used during construction of shop
  	catalogModel.invalidateAll()
  	catalogModel.flush()
  	shopModel
  }) 

  private var previewSequenceNr = 0
  private var publishedSequenceNr = 0
  CacheInvalidator.start
  
  def findAllCatalogs : Seq[jpa.catalog.Catalog] =
  	WebshopModel.dao.getEntityManager.createQuery("select catalog from Catalog catalog").getResultList.asInstanceOf[java.util.List[jpa.catalog.Catalog]]
 
  def findAllShops : Seq[jpa.shop.Shop] =
    WebshopModel.dao.getEntityManager.createQuery("select shop from Shop shop").getResultList.asInstanceOf[java.util.List[jpa.shop.Shop]]

  def findAllStagingAreas : Seq[jpa.catalog.StagingArea] =
	  WebshopModel.dao.getEntityManager.createQuery("select staging from StagingArea staging").getResultList.asInstanceOf[java.util.List[jpa.catalog.StagingArea]]
	
	def checkValid = {
		findAllStagingAreas.find(_.getName == PerformStaging.STAGING_AREA_PREVIEW) match {
			case Some(sa) => 
				val sequenceNr : Int = WebshopModel.dao.getOrCreateStagingStatus(catalog, sa).getUpdateSequenceNr.intValue
				if (previewSequenceNr != sequenceNr) {
					println("Clearing shop cache");
					previewSequenceNr = sequenceNr
					map.clear()
//					WebshopModel.dao.getEntityManagerFactory().getCache().evictAll();
				}
			case _ =>
		}
		findAllStagingAreas.find(_.getName == PerformStaging.STAGING_AREA_PUBLISHED) match {
			case Some(sa) => 
				val sequenceNr : Int = WebshopModel.dao.getOrCreateStagingStatus(catalog, sa).getUpdateSequenceNr.intValue
				if (publishedSequenceNr != sequenceNr) {
					println("Clearing shop cache");
					publishedSequenceNr = sequenceNr
					map.clear()
//					WebshopModel.dao.getEntityManagerFactory().getCache().evictAll();
				}
			case _ =>
		}
	}
}

object CacheInvalidator extends Thread {
	override def run() {
		while (true) {
			try {
				WebshopCache.checkValid
				Thread.sleep(5000);
			} catch {
				case e: InterruptedException => throw e
				case _ =>
			}
		}
	}
}

class WebshopData (val catalog : CatalogModel, val shop : jpa.shop.Shop, val staging : jpa.catalog.StagingArea, val language : String) {

  def items : Set[ItemModel] = 
  	catalog.root.getChildExtent.toSet filter (_.isVisible(staging, shop, language))
//  
//  val itemsEntities : Set[jpa.catalog.Item] = 
//  	items.map(_.getEntity)
  
  def item(item : jpa.catalog.Item) = catalog.getItem(item.getId)
  
  val excludedProperties : Set[jpa.catalog.Property] = 
    shop.getExcludedProperties toSet

  def isExcluded(property : PropertyModel) = excludedProperties.contains(property.getEntity)
    
  def promotions : Set[jpa.shop.Promotion] = 
    shop.getPromotions toSet
//    
//  private def navigation(nav : Iterable[jpa.shop.Navigation]) : Seq[Seq[jpa.shop.Navigation]] = {
//    val sorted = nav.toSeq.sortBy(_.getIndex.getOrElse(0))
//    val filled = sorted.filter(nav => itemsEntities.contains(nav.getCategory))
//    val filledSeq = if (filled.isEmpty) Seq() else Seq(sorted.filter(nav => itemsEntities.contains(nav.getCategory)))
//    val empty = sorted.filter(nav => !itemsEntities.contains(nav.getCategory))
//    if (empty.isEmpty) filledSeq
//    else filledSeq ++ empty.flatMap(nav => navigation(nav.getSubNavigation))
//    println("***************************************")
//    println(nav)
//    Seq(sorted)
//  }
    
  def topLevelNavigation = Seq(shop.getNavigation.toSeq)
  
//  def topLevelCategories : Set[jpa.catalog.Category] = {
//  	println(topLevelNavigation.toSeq)
//  	topLevelNavigation.flatMap(_.map(_.getCategory)).toSet.filter(itemsEntities)
//  }

//  def allItemEntities : Set[jpa.catalog.Item] = 
//  	items.map(_.getEntity)
  
//  def allPropertyValues : Set[jpa.catalog.PropertyValue] = 
//  	allItemEntities.flatMap(_.getPropertyValues)
  
  def template(obj : Object, template : String) : Option[NodeSeq] = {
    None
  }  
  
  /**
   * The product seq is ordered by volume, high volume products come first.
   * @return
   */
  def alsoBought : Map[ItemModel, Seq[ItemModel]] = {
  	val allOrders : Seq[jpa.order.Order] = catalog.dao.getOrdersWithProducts(shop, 200, TimeUnit.DAYS)
  	val productsByUser = mutable.Map[jpa.party.User, mutable.Map[jpa.catalog.Product, Int]]()
  	for (order <- allOrders) {
  		val products = productsByUser.getOrElseUpdate(order.getUser, mutable.Map[jpa.catalog.Product, Int]())
  		for (product <- order.getProductOrders) {
  			val volume = products.getOrElse(product.getProduct, 0) + product.getVolume.intValue
  			products += ((product.getProduct, volume))
  		}
  	}
    val productsByProduct = mutable.Map[jpa.catalog.Product, mutable.Map[jpa.catalog.Product, Int]]()
    for ((user, userProducts) <- productsByUser) {
    	for (productKey <- userProducts.keys) {
    		for ((product, volume) <- userProducts if product != productKey) {
    			val productProducts = productsByProduct.getOrElseUpdate(productKey, mutable.Map[jpa.catalog.Product, Int]())
    			val volume2 = productProducts.getOrElse(product, 0) + volume
    			productProducts += ((product, volume2))
    		}
    	}
    }
    val result = for ((product, products) <- productsByProduct) 
    	yield (catalog.getItem(product.getId), products.toSeq.sortBy(_._2).map(t => catalog.getItem(t._1.getId)).reverse)
    
    result.toMap
  }
}
