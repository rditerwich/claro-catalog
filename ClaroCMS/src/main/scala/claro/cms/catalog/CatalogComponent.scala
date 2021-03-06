package claro.cms.catalog

import claro.cms.{Cms,Component,Template,ResourceLocator,Scope}
import claro.cms.catalog.data.StandardCatalogData
import claro.common.util.Conversions._
import claro.jpa.catalog._

class CatalogComponent extends Component {

  val prefix = "catalog"
    
  bindings.append {
    case _ : CatalogComponent => Map(
      "products" -> CatalogDao.findProducts(Nil, Nil) -> "product"
    )
    case product : Product => Map(
      "id" -> product.getId.getOrElse(-1)
    )
  }
  
  rewrite.append {
     case path => path
  }
  
  override def boot { 
    CatalogDao.createInitialCatalog(StandardCatalogData.createSampleData)
//    CatalogDao.withCatalog(CatalogDao.catalog("Catalog").get) {
//        StandardCatalogData.createSampleData
//    }
  }
}