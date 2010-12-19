package claro.catalog.importing.command;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import claro.jpa.importing.ImportDefinition;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Paging;

public class GetImportDefinitions implements Command<GetImportDefinitionsResult> {

	private static final long serialVersionUID = 1L;
	
	public Long importDefinitionId = null;
	
	public String importDefinitionName = null;
	
	public Paging paging = Paging.NO_PAGING;
	
	public boolean includeLastRunStatistics;
	
	public boolean includeDefinitionDetails = false;
	
	public void checkValid() throws CommandValidationException {
		if (importDefinitionId != null) validate(paging.equals(Paging.NO_PAGING));
		if (!paging.equals(Paging.NO_PAGING)) validate(importDefinitionId == null);
		validate(importDefinitionId == null || importDefinitionName == null);
	}
	
	public static class Result {
		public List<ImportDefinition> importDefinitions;
	}
}
