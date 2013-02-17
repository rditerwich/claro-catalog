package claro.catalog.manager

import metaphor.core.IProduct
import metaphor.psm.domaintordbms.DatabaseSchema

method String CatalogXSDatabaseSchema(IProduct product) extends DatabaseSchema
    return "catalog"
/method
