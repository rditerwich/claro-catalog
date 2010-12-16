package claro.catalog.model;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.CatalogServer;
import claro.catalog.command.importing.UpdateImportDefinition;
import claro.catalog.command.importing.UpdateImportDefinition.Result;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import easyenterprise.lib.command.CommandException;

public class ImportTests extends CatalogTestBase {

	@Test
	public void test() throws IOException, SQLException, CommandException {
		CatalogServer server = getServer();
		UpdateImportDefinition update = new UpdateImportDefinition();
		update.importDefinition = new ImportDefinition();
		update.importDefinition.setName("my-import");
		update.importDefinition.setImportUrl("file:///test");
		ImportCategory importCategory = new ImportCategory();
		importCategory.setExpression("Wines");
		update.importDefinition.getCategories().add(importCategory );
		Result result = server.execute(update);
	}
}
