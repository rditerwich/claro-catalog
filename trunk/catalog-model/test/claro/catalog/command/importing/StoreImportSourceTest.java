package claro.catalog.command.importing;

import static easyenterprise.lib.util.CollectionUtil.first;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.TabularFileFormat;
import easyenterprise.lib.command.CommandException;

public class StoreImportSourceTest extends CatalogTestBase {

	@Test
	public void test() throws IOException, SQLException, CommandException {
		ensureDatabaseCreated();
		
		ImportSource importSource = new ImportSource();
		ImportRules rules = new ImportRules();
		importSource.getRules().add(rules);
		ImportProducts importProducts = new ImportProducts();
		rules.setImportProducts(importProducts);
		TabularFileFormat fileFormat = new TabularFileFormat();
		rules.setFileFormat(fileFormat);

		importSource.setName("my-import");
		importSource.setImportUrl("file:///test");
		importSource.setPriority(0);
		ImportCategory importCategory = new ImportCategory();
		importCategory.setCategoryExpression("Wines");
		importProducts.getCategories().add(importCategory);
		importProducts.setOutputChannelExpression("");
		assertNull(importSource.getId());
		assertNull(importCategory.getId());
		StoreImportSource update = new StoreImportSource(importSource);
		ImportSource def = executeCommand(update).importSource;
		assertNotNull(def.getId());
		assertFalse(def.getRules().isEmpty());
		assertNotNull(first(def.getRules()).getImportProducts());
		assertNotNull(first(first(def.getRules()).getImportProducts().getCategories()).getId());
		
		GetImportSources query = new GetImportSources();
		query.ImportSourceName = "my-imp";
		GetImportSources.Result ImportSources = executeCommand(query);
	}
}
