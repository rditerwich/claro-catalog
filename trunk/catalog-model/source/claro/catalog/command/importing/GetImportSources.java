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
	
	public Long ImportSourceId = null;
	
	public String ImportSourceName = null;
	
	public Paging paging = Paging.NO_PAGING;
	
	public boolean includeLastRunStatistics;
	
	public boolean includeDefinitionDetails = false;
	
	public void checkValid() throws CommandValidationException {
		if (ImportSourceId != null) validate(paging.equals(Paging.NO_PAGING));
		if (!paging.equals(Paging.NO_PAGING)) validate(ImportSourceId == null);
		validate(ImportSourceId == null || ImportSourceName == null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public List<ImportSource> ImportSources;
	}
}
