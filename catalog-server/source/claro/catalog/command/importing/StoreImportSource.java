package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;
import static easyenterprise.lib.util.CollectionUtil.notNull;

import java.util.List;

import claro.catalog.command.importing.StoreImportSource.Result;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class StoreImportSource implements Command<Result> {

	private static final long serialVersionUID = 1L;

	protected StoreImportSource() {
	}
	
	public StoreImportSource(ImportSource importSource) {
		this.importSource = importSource;
	}
	
	/**
	 * Import source to be stored, recursively. Deleted children are not detected, please
	 * use ...ToBeRemoved collections
	 */
	public ImportSource importSource;
	
	/**
	 * Remove the entire import source. The field {@link #skipImportSource}
	 * should be set to false, {@link #importCategoriesToBeRemoved} and
	 * {@link #importPropertiesToBeRemoved} should be empty.
	 */
	public boolean removeImportSource;
	public List<ImportRules> importRulesToBeRemoved;
	public List<ImportProducts> importProductsToBeRemoved;
	public List<ImportCategory> importCategoriesToBeRemoved;
	public List<ImportProperty> importPropertiesToBeRemoved;
	
	public void checkValid() throws CommandValidationException {
		validate (importSource != null, "No import source specified");
		if (removeImportSource) validate(isEmpty(importRulesToBeRemoved), "Nested removes are redundant");
		if (removeImportSource) validate(isEmpty(importProductsToBeRemoved), "Nested removes are redundant");
		if (removeImportSource) validate(isEmpty(importCategoriesToBeRemoved), "Nested removes are redundant");
		if (removeImportSource) validate(isEmpty(importPropertiesToBeRemoved), "Nested removes are redundant");
		for (ImportRules rules : notNull(importRulesToBeRemoved)) 
			validate(rules.getImportSource() == null || rules.getImportSource().getId().equals(importSource.getId()), "Import rules do not belong to import source");
		for (ImportCategory cat : notNull(importCategoriesToBeRemoved)) 
			validate(cat.getImportProducts() != null && cat.getImportProducts().getId() != null, "Import products not specified for category");
		for (ImportProperty prop : notNull(importPropertiesToBeRemoved))
			validate(prop.getImportProducts() != null && prop.getImportProducts().getId() != null, "Import products not specified for property");
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public ImportSource importSource;
	}
}
