package claro.catalog.command.importing;

import static java.util.Collections.singleton;

import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.command.importing.StoreImportSource.Result;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.catalog.model.test.util.TestCatalogModel;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.TabularFileFormat;
import claro.jpa.jobs.JobResult;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.sexpr.Constant;

public class PerformImportTest extends CatalogTestBase {

	
	@Test
	public void test() throws SQLException, CommandException {

		ensureDatabaseCreated();
		TestCatalogModel model = getCatalogModel();
		
		// create an import definition
		ImportSource importSource = new ImportSource();
		ImportRules rules = new ImportRules();
		importSource.getRules().add(rules);
		ImportProducts importProducts = new ImportProducts();
		rules.setImportProducts(importProducts);
		TabularFileFormat fileFormat = new TabularFileFormat();
		rules.setFileFormat(fileFormat);
		
		importProducts.setMatchProperty(model.articleNumberProperty.getEntity());
		
		importSource.setName("Import Test Products");
		fileFormat.setHeaderLine(true);
		ImportProperty propDef = new ImportProperty();
		propDef.setProperty(model.articleNumberProperty.getEntity());
		propDef.setValueExpression("#Klantartikelnummer");
		importProducts.getProperties().add(propDef);
		propDef = new ImportProperty();
		propDef.setProperty(model.nameProperty.getEntity());
		propDef.setValueExpression("#omschrijving");
		importProducts.getProperties().add(propDef);
		
		// create a category
		String categoryName = "HP Ink Cartridges";
		ItemModel category = model.createCategory();
		category.setParents(singleton(model.getRootItem()));
		PropertyModel property = category.findProperty(model.nameProperty.getEntity(), true);
		property.setValue(importSource, null, null, null, categoryName);
		
		ImportCategory importCat = new ImportCategory();
		importCat.setCategoryExpression(Constant.constant(categoryName));
		importProducts.getCategories().add(importCat);

		// create an import definition
		StoreImportSource update = new StoreImportSource();
		update.importSource = importSource;
		Result updateResult = executeCommand(update);
		
		
		// perform update
		PerformImport performImport = new PerformImport();
		performImport.catalogId = TEST_CATALOG_ID;
		performImport.importSourceId = updateResult.importSource.getId();
		performImport.importUrl = new Constant(getClass().getResource("sample-products.csv").toString()).toString();
		
		System.out.println("FIRST RUN:");
		PerformImport.Result result = executeCommand(performImport);
		for (JobResult jobResult : result.jobResults) {
			System.out.println(jobResult.getLog());
		}
		System.out.println("SECOND RUN:");
		result = executeCommand(performImport);
		for (JobResult jobResult : result.jobResults) {
			System.out.println(jobResult.getLog());
		}
	}
}
