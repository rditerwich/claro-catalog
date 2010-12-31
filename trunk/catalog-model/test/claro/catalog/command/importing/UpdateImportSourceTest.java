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
		update.importSource = new ImportSource();
		update.importSource.setName("my-import");
		update.importSource.setImportUrlExpression("file:///test");
		update.importSource.setPriority(0);
		ImportCategory importCategory = new ImportCategory();
		importCategory.setCategoryExpression("Wines");
		update.importSource.getCategories().add(importCategory);
		assertNull(update.importSource.getId());
		assertNull(importCategory.getId());
		ImportSource def = executeCommand(update).importSource;
		assertNotNull(def.getId());
		assertNotNull(first(def.getCategories()).getId());
		
		GetImportSources query = new GetImportSources();
		query.ImportSourceName = "my-imp";
		GetImportSources.Result ImportSources = executeCommand(query);
	}
}
