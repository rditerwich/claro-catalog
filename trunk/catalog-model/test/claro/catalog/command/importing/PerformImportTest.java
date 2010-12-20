package claro.catalog.command.importing;

import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.TabularImportDefinition;

public class PerformImportTest extends CatalogTestBase {

	
	@Test
	public void test() throws SQLException {
		
		CatalogModel model = getCatalogModel();
		
		// create an import definition
		TabularImportDefinition importDef = new TabularImportDefinition();
		importDef.setName("Import Test Products");
		importDef.setHeaderLine(true);
		ImportProperty propDef = new ImportProperty();
		propDef.setProperty(model.articleNumberProperty);
		propDef.setExpression("#Klantartikelnummer");
		importDef.getProperties().add(propDef);
		propDef.setProperty(model.descriptionProperty);
		propDef.setExpression("#omschrijving");
		importDef.getProperties().add(propDef);
		
		// create a category
		ItemModel category = model.createCategory("HP Cartridge");
		category.findOrCreateProperty()
		ImportCategory importCat = new ImportCategory();
		importCat.setExpression(category.)
		importDef.setCategories(Collection)
		
		UpdateImportDefinition update = new UpdateImportDefinition();
		update.importDefinition = importDef;
		
	}
}
