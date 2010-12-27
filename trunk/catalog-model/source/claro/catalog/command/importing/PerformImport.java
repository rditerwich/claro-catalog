package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import claro.jpa.importing.ImportDefinition;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class PerformImport implements Command<PerformImport.Result> {

	private static final long serialVersionUID = 1L;
	
	public Long catalogId;
	
	public Long importDefinitionId;
	
	/**
	 * Override import definition attributes, but instead read
	 * data from this url. Optional.
	 */
	public String importUrl = null;

	/**
	 * Overwrite 
	 */
	public boolean overwriteAllImportSources = false;
	
	public boolean overwriteLastImportSource = false;
	
	public void checkValid() throws CommandValidationException {
		validate(catalogId != null);
		validate(importDefinitionId != null);
		validate(!overwriteAllImportSources || !overwriteLastImportSource);
	}
	
	public static class Result implements CommandResult {
		public List<ImportDefinition> importDefinitions;
	}
}
