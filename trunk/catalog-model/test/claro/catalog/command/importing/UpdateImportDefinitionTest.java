package claro.catalog.command.importing;

import static easyenterprise.lib.util.CollectionUtil.first;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import easyenterprise.lib.command.CommandException;

public class UpdateImportDefinitionTest extends CatalogTestBase {

	@Test
	public void test() throws IOException, SQLException, CommandException {
		ensureDatabaseCreated();
		UpdateImportDefinition update = new UpdateImportDefinition();
		update.importDefinition = new ImportDefinition();
		update.importDefinition.setName("my-import");
		update.importDefinition.setImportUrl("file:///test");
		update.importDefinition.setPriority(0);
		ImportCategory importCategory = new ImportCategory();
		importCategory.setExpression("Wines");
		update.importDefinition.getCategories().add(importCategory);
		assertNull(update.importDefinition.getId());
		assertNull(importCategory.getId());
		ImportDefinition def = executeCommand(update).importDefinition;
		assertNotNull(def.getId());
		assertNotNull(first(def.getCategories()).getId());
		
		GetImportDefinitions query = new GetImportDefinitions();
		query.importDefinitionName = "my-imp";
		GetImportDefinitions.Result importDefinitions = executeCommand(query);
	}
}
