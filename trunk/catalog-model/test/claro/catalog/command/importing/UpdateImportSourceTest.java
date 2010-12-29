package claro.catalog.command.importing;

import static easyenterprise.lib.util.CollectionUtil.first;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.command.CommandException;

public class UpdateImportSourceTest extends CatalogTestBase {

	@Test
	public void test() throws IOException, SQLException, CommandException {
		ensureDatabaseCreated();
		UpdateImportSource update = new UpdateImportSource();
		update.ImportSource = new ImportSource();
		update.ImportSource.setName("my-import");
		update.ImportSource.setImportUrlExpression("file:///test");
		update.ImportSource.setPriority(0);
		ImportCategory importCategory = new ImportCategory();
		importCategory.setCategoryExpression("Wines");
		update.ImportSource.getCategories().add(importCategory);
		assertNull(update.ImportSource.getId());
		assertNull(importCategory.getId());
		ImportSource def = executeCommand(update).ImportSource;
		assertNotNull(def.getId());
		assertNotNull(first(def.getCategories()).getId());
		
		GetImportSources query = new GetImportSources();
		query.ImportSourceName = "my-imp";
		GetImportSources.Result ImportSources = executeCommand(query);
	}
}
