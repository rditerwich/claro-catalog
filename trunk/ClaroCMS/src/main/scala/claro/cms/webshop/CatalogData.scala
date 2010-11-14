package claro.cms.webshop

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.xml.NodeSeq 
import java.util.Locale

import claro.jpa
import claro.cms.Cms
import claro.common.util.Conversions._
import claro.catalog.CatalogUtil._

class CatalogData(val catalog : jpa.catalog.Catalog) {

  val templateObjectCache = new mutable.HashMap[Tuple2[Object, String], NodeSeq]
  val templateClassCache = new mutable.HashMap[Tuple2[Class[_], String], NodeSeq]

  val items : Set[jpa.catalog.Item] =
  	catalog.getItems.toSet

  val itemChildren : Map[jpa.catalog.Item, Seq[jpa.catalog.Item]] =
  	Map(items.toSeq.map(item => (item, getChildren(item).toSeq)):_*).
  		withDefault(_ => Seq.empty)
      	
	val itemParents : Map[jpa.catalog.Item, Seq[jpa.catalog.Item]] =
		Map(items.toSeq.map(item => (item, getParents(item).toSeq)):_*).
			withDefault(_ => Seq.empty)
  			
	val itemChildExtent : Map[jpa.catalog.Item, Seq[jpa.catalog.Item]] =
		Map(items.toSeq.map(item => (item, getChildExtent(item, false).toSeq)):_*).
			withDefault(_ => Seq.empty)
		
	val itemParentExtent : Map[jpa.catalog.Item, Seq[jpa.catalog.Item]] =
		Map(items.toSeq.map(item => (item, getParentExtent(item, false).toSeq)):_*).
			withDefault(_ => Seq.empty)

  val itemProperties : Map[jpa.catalog.Item, Seq[jpa.catalog.Property]] =
    Map(items.toSeq.map(item => (item, item.getProperties.toSeq)):_*).
      withDefault(_ => Seq.empty)

  val itemPropertyExtent : Map[jpa.catalog.Item, Seq[jpa.catalog.Property]] = 
  	Map(items.toSeq.map(item =>
      (item, (itemParentExtent(item) ++ Seq(item)).
          flatMap(itemProperties(_)))):_*).
          	withDefault(_ => Seq.empty)
}
