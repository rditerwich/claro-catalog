package claro.cms.webshop

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.xml.NodeSeq 
import java.util.Locale
import java.util.concurrent.ConcurrentMap
import claro.catalog.model.CatalogModel
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
	lazy val catalogModel = new CatalogModel(findAllCatalogs.find(_.getName == catalogName).get.getId, WebshopModel.dao)
	
  private val map : ConcurrentMap[(Boolean, String), Shop] = MapMaker(10, MapMaker.STRONG, MapMaker.SOFT, key => {
  	val stagingAreaName = if (key._1) CatalogModel.STAGING_AREA_PREVIEW else CatalogModel.STAGING_AREA_PUBLISHED
  	val stagingArea = findAllStagingAreas.find(_.getName == stagingAreaName).get
  	val language = key._2
  	val shop = findAllShops.find(_.getName == shopName).get
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
		val catalog = catalogModel.getCatalog
		findAllStagingAreas.find(_.getName == CatalogModel.STAGING_AREA_PREVIEW) match {
			case Some(sa) => 
				val sequenceNr : Int = WebshopModel.dao.getOrCreateStagingStatus(catalog, sa).getUpdateSequenceNr.intValue
				if (previewSequenceNr != sequenceNr) {
					previewSequenceNr = sequenceNr
					map.clear();
				}
			case _ =>
		}
		findAllStagingAreas.find(_.getName == CatalogModel.STAGING_AREA_PUBLISHED) match {
			case Some(sa) => 
				val sequenceNr : Int = WebshopModel.dao.getOrCreateStagingStatus(catalog, sa).getUpdateSequenceNr.intValue
				if (publishedSequenceNr != sequenceNr) {
					publishedSequenceNr = sequenceNr
					map.clear();
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
  
  def item(item : jpa.catalog.Item) = catalog.getItem(item.getId)
  
  val excludedProperties : Set[jpa.catalog.Property] = 
    shop.getExcludedProperties toSet

  def isExcluded(property : PropertyModel) = excludedProperties.contains(property.getEntity)
    
  val promotions : Set[jpa.shop.Promotion] = 
    shop.getPromotions toSet
    
  private def navigation(nav : Iterable[jpa.shop.Navigation]) : Seq[Seq[jpa.shop.Navigation]] = {
    val sorted = nav.toSeq.sortBy(_.getIndex.getOrElse(0))
    val filled = sorted.filter(_.getCategory != null)
    val filledSeq = if (filled.isEmpty) Seq() else Seq(sorted.filter(_.getCategory != null))
    val empty = sorted.filter(_.getCategory == null)
    if (empty.isEmpty) filledSeq
    else filledSeq ++ empty.flatMap(nav => navigation(nav.getSubNavigation))
  }
    
  def topLevelNavigation = navigation(shop.getNavigation)
  
  def topLevelCategories : Set[jpa.catalog.Category] = 
  	topLevelNavigation.flatMap(_.map(_.getCategory)).toSet

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
