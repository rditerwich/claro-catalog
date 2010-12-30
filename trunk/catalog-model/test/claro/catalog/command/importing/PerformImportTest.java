package claro.catalog.command.importing;

import static java.util.Collections.singleton;

import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.command.importing.UpdateImportSource.Result;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.TabularImportSource;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.sexpr.Constant;

public class PerformImportTest extends CatalogTestBase {

	
	@Test
	public void test() throws SQLException, CommandException {

		ensureDatabaseCreated();
		CatalogModel model = getCatalogModel();
		
		// create an import definition
		TabularImportSource importDef = new TabularImportSource();
		importDef.setMatchProperty(model.articleNumberProperty.getEntity());
		
		importDef.setName("Import Test Products");
		importDef.setHeaderLine(true);
		ImportProperty propDef = new ImportProperty();
		propDef.setProperty(model.articleNumberProperty.getEntity());
		propDef.setValueExpression("#Klantartikelnummer");
		importDef.getProperties().add(propDef);
		propDef = new ImportProperty();
		propDef.setProperty(model.descriptionProperty.getEntity());
		propDef.setValueExpression("#omschrijving");
		importDef.getProperties().add(propDef);
		
		// create a category
		String categoryName = "HP Ink Cartridges";
		ItemModel category = model.createCategory();
		category.setParents(singleton(model.getRootItem()));
		PropertyModel property = category.findProperty(model.nameProperty.getEntity(), true);
		property.setValue(null, null, null, categoryName);
		
		ImportCategory importCat = new ImportCategory();
		importCat.setCategoryExpression(Constant.constant(categoryName));
		importDef.getCategories().add(importCat);

		// create an import definition
		UpdateImportSource update = new UpdateImportSource();
		update.importSource = importDef;
		Result updateResult = executeCommand(update);
		
		
		// perform update
		PerformImport performImport = new PerformImport();
		performImport.catalogId = TEST_CATALOG_ID;
		performImport.ImportSourceId = updateResult.ImportSource.getId();
		performImport.importUrl = new Constant(getClass().getResource("sample-products.csv").toString()).toString();
		System.out.println("FIRST RUN:");
		PerformImport.Result result = executeCommand(performImport);
		System.out.println(result.log);
		System.out.println("SECOND RUN:");
		result = executeCommand(performImport);
		System.out.println(result.log);
	}
}
