package claro.catalog.command.importing;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.TabularImportDefinition;

public class PerformImportTest extends CatalogTestBase {

	
	@Test
	public void test() {
		
		// create an import definition
		TabularImportDefinition importDef = new TabularImportDefinition();
		importDef.setName("Import Test Products");
		importDef.setHeaderLine(true);
		ImportProperty propDef = new ImportProperty();
		
		UpdateImportDefinition update = new UpdateImportDefinition();
		update.importDefinition = importDef;
		
	}
}
