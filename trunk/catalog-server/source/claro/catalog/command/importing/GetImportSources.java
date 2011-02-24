package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import claro.jpa.importing.ImportSource;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Paging;

public class GetImportSources implements Command<GetImportSources.Result> {

	private static final long serialVersionUID = 1L;
	
	public Long importSourceId = null;
	
	public String ImportSourceName = null;
	
	public Paging paging = Paging.NO_PAGING;
	
	public boolean includeLastRunStatistics;
	
	public boolean includeDefinitionDetails = false;
	
	public void checkValid() throws CommandValidationException {
		if (importSourceId != null) validate(paging.equals(Paging.NO_PAGING));
		if (!paging.equals(Paging.NO_PAGING)) validate(importSourceId == null);
		validate(importSourceId == null || ImportSourceName == null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public List<ImportSource> importSources;
	}
}
