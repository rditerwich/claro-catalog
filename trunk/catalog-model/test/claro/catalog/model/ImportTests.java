package claro.catalog.model;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.command.importing.GetImportDefinitions;
import claro.catalog.command.importing.UpdateImportDefinition;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import easyenterprise.lib.command.CommandException;

public class ImportTests extends CatalogTestBase {

	@Test
	public void test() throws IOException, SQLException, CommandException {
		UpdateImportDefinition update = new UpdateImportDefinition();
		update.importDefinition = new ImportDefinition();
		update.importDefinition.setName("my-import");
		update.importDefinition.setImportSourceName("my-import");
		update.importDefinition.setImportUrl("file:///test");
		ImportCategory importCategory = new ImportCategory();
		importCategory.setExpression("Wines");
		update.importDefinition.getCategories().add(importCategory);
		executeCommand(update);
		
		GetImportDefinitions query = new GetImportDefinitions();
		query.importDefinitionName = "my-imp";
		GetImportDefinitions.Result importDefinitions = executeCommand(query);
	}
}
