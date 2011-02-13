package claro.cms.webshop

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.xml.NodeSeq 
import java.util.Locale
import java.util.concurrent.ConcurrentMap
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
	def catalogModel = new CatalogModel(catalog.getId, WebshopModel.dao)
	
  private val map : ConcurrentMap[(Boolean, String), Shop] = MapMaker(10, MapMaker.STRONG, MapMaker.SOFT, key => {
  	val stagingAreaName = if (key._1) PerformStaging.STAGING_AREA_PREVIEW else PerformStaging.STAGING_AREA_PUBLISHED
  	val stagingArea = WebshopModel.dao.getOrCreateStagingArea(stagingAreaName)
  	val language = key._2
  	println("SHOPS:" + findAllShops.map(_.getName).mkString(","))
  	println("FINDING SHOP:" + shopName)
  	val shop = findAllShops.find(_.getName == shopName).get
  	println("SHOP FOUND:" + shop)
  	new Shop(new WebshopData(catalogModel, shop, stagingArea, language))
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

  val templateObjectCache = new mutable.HashMap[Tuple2[Object, String], NodeSeq]
  val templateClassCache = new mutable.HashMap[Tuple2[Class[_], String], NodeSeq]

  val items : Set[ItemModel] = 
  	catalog.root.getChildExtent.toSet filter (_.isVisible(staging, shop, language))
  
  val itemsEntities : Set[jpa.catalog.Item] = 
  	items.map(_.getEntity)
  
  def item(item : jpa.catalog.Item) = catalog.getItem(item.getId)
  
  val excludedProperties : Set[jpa.catalog.Property] = 
    shop.getExcludedProperties toSet

  def isExcluded(property : PropertyModel) = excludedProperties.contains(property.getEntity)
    
  val promotions : Set[jpa.shop.Promotion] = 
    shop.getPromotions toSet
    
  private def navigation(nav : Iterable[jpa.shop.Navigation]) : Seq[Seq[jpa.shop.Navigation]] = {
    val sorted = nav.toSeq.sortBy(_.getIndex.getOrElse(0))
    val filled = sorted.filter(nav => itemsEntities.contains(nav.getCategory))
    val filledSeq = if (filled.isEmpty) Seq() else Seq(sorted.filter(nav => itemsEntities.contains(nav.getCategory)))
    val empty = sorted.filter(nav => !itemsEntities.contains(nav.getCategory))
    if (empty.isEmpty) filledSeq
    else filledSeq ++ empty.flatMap(nav => navigation(nav.getSubNavigation))
    println("***************************************")
    println(nav)
    Seq(sorted)
  }
    
  def topLevelNavigation = Seq(shop.getNavigation.toSeq)
  
  def topLevelCategories : Set[jpa.catalog.Category] = {
  	println(topLevelNavigation.toSeq)
  	topLevelNavigation.flatMap(_.map(_.getCategory)).toSet.filter(itemsEntities)
  }

  def allItemEntities : Set[jpa.catalog.Item] = 
  	items.map(_.getEntity)
  
  def allPropertyValues : Set[jpa.catalog.PropertyValue] = 
  	allItemEntities.flatMap(_.getPropertyValues)
  
  val mediaPropertyValues : Seq[jpa.catalog.PropertyValue] = 
      allPropertyValues filter (_.getProperty.getType == jpa.catalog.PropertyType.Media) filter(_.getMediaValue != null) toSeq
  
  val mediaValues : Map[Long, (String, Array[Byte])] =
    Map(mediaPropertyValues map (v => (v.getId.longValue, (v.getMimeType, v.getMediaValue))):_*)
    	


  def template(obj : Object, template : String) : Option[NodeSeq] = {
    None
  }  
}
