package claro.catalog.command.importing;

import claro.catalog.command.importing.UpdateImportDefinition.Result;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportProperty;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.util.Tuple;

public class UpdateImportDefinition implements Command<Result> {

	/**
	 * Only basic fields will be stored, no recursion
	 */
	public ImportDefinition importDefinition;
	
	public Tuple<Long, ImportCategory> importCategory;
	
	public Tuple<Long, ImportProperty> importProperty;
	
	
	public static class Result implements CommandResult {

	}
}
