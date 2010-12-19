package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;

import java.util.List;

import claro.catalog.command.importing.UpdateImportDefinition.Result;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportProperty;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class UpdateImportDefinition implements Command<Result> {

	/**
	 * Don't update import definition instance. Only use {@link #importDefinition}
	 * to find nested import-categories or import-properties, or to store the id
	 * for removing import-categories or import-properties.
	 */
	public boolean skipImportDefinition;
	
	/**
	 * Remove the entire import definition. The field {@link #skipImportDefinition}
	 * should be set to false, {@link #importCategoriesToBeRemoved} and
	 * {@link #importPropertiesToBeRemoved} should be empty.
	 */
	public boolean remove;
	
	/**
	 * Only basic fields will be stored, no recursion
	 */
	public ImportDefinition importDefinition;
	
	public List<ImportCategory> importCategoriesToBeRemoved;
	
	public List<ImportProperty> importPropertiesToBeRemoved;
	
	public void checkValid() throws CommandValidationException {
		validate (importDefinition != null);
		if (remove) validate(!skipImportDefinition);
		if (remove) validate(isEmpty(importCategoriesToBeRemoved));
		if (remove) validate(isEmpty(importPropertiesToBeRemoved));
		if (skipImportDefinition) validate(importDefinition.getId() != null);
		if (!isEmpty(importCategoriesToBeRemoved)) validate(importDefinition.getId() != null);
		if (!isEmpty(importPropertiesToBeRemoved)) validate(importDefinition.getId() != null);
	}
	
	public static class Result implements CommandResult {
		public ImportDefinition importDefinition;
	}
}
