package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;

import java.util.List;

import claro.catalog.command.importing.StoreImportSource.Result;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class StoreImportSource implements Command<Result> {

	private static final long serialVersionUID = 1L;

	/**
	 * Don't update import definition instance. Only use {@link #importSource}
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
	public ImportSource importSource;
	
	public List<ImportCategory> importCategoriesToBeRemoved;
	
	public List<ImportProperty> importPropertiesToBeRemoved;
	
	public void checkValid() throws CommandValidationException {
		validate (importSource != null);
		if (remove) validate(!skipImportSource);
		if (remove) validate(isEmpty(importCategoriesToBeRemoved));
		if (remove) validate(isEmpty(importPropertiesToBeRemoved));
		if (skipImportSource) validate(importSource.getId() != null);
		if (!isEmpty(importCategoriesToBeRemoved)) validate(importSource.getId() != null);
		if (!isEmpty(importPropertiesToBeRemoved)) validate(importSource.getId() != null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public ImportSource importSource;
	}
}
