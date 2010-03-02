package agilexs.catalogxs.presentation.model

import scala.collection.{mutable, Set, Map}
import scala.xml.NodeSeq 

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.util.ProjectionMap
import agilexs.catalogxs.presentation.model.Conversions._ 

class TemplateCache private (val catalogCache : CatalogCache, val catalog : jpa.Catalog, val view : jpa.CatalogView, val locale : String) {

  type Templates = Map[String, jpa.Template]
  
  val catalogTemplates : Set[jpa.Template] = 
    catalog.getTemplates toSet
  
  val viewTemplates : Set[jpa.Template] = 
    view.getTemplates toSet
  
  val productGroupTemplates : Map[jpa.ProductGroup, Templates] =
    mutable.Map((for (group <- catalogCache.productGroups) 
      yield (group -> byName(group.getTemplates))):_*)
  
  val productGroupTemplates2 : Map[jpa.ProductGroup, Templates] = 
    catalogCache.productGroups makeMap ((g : jpa.ProductGroup) => byName(g.getTemplates))
  
  private def byName(templates : Iterable[jpa.Template]) : Templates =
    templates makeMapReverse (_.getName)
}