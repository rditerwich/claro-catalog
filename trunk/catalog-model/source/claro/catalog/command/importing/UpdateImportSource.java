package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;

import java.util.List;

import claro.catalog.command.importing.UpdateImportSource.Result;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class UpdateImportSource implements Command<Result> {

	private static final long serialVersionUID = 1L;

	/**
	 * Don't update import definition instance. Only use {@link #ImportSource}
	 * to find nested import-categories or import-properties, or to store the id
	 * for removing import-categories or import-properties.
	 */
	public boolean skipImportSource;
	
	/**
	 * Remove the entire import definition. The field {@link #skipImportSource}
	 * should be set to false, {@link #importCategoriesToBeRemoved} and
	 * {@link #importPropertiesToBeRemoved} should be empty.
	 */
	public boolean remove;
	
	/**
	 * Only basic fields will be stored, no recursion
	 */
	public ImportSource ImportSource;
	
	public List<ImportCategory> importCategoriesToBeRemoved;
	
	public List<ImportProperty> importPropertiesToBeRemoved;
	
	public void checkValid() throws CommandValidationException {
		validate (ImportSource != null);
		if (remove) validate(!skipImportSource);
		if (remove) validate(isEmpty(importCategoriesToBeRemoved));
		if (remove) validate(isEmpty(importPropertiesToBeRemoved));
		if (skipImportSource) validate(ImportSource.getId() != null);
		if (!isEmpty(importCategoriesToBeRemoved)) validate(ImportSource.getId() != null);
		if (!isEmpty(importPropertiesToBeRemoved)) validate(ImportSource.getId() != null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public ImportSource ImportSource;
	}
}
