package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import claro.jpa.importing.ImportJobResult;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Paging;

public class GetImportSourceHistory implements Command<GetImportSourceHistory.Result> {

	private static final long serialVersionUID = 1L;
	
	public Long importSourceId;

	public Long jobResultId = null;
	
	public Paging paging = Paging.NO_PAGING;
	
	public void checkValid() throws CommandValidationException {
		validate(importSourceId != null);
		if (jobResultId != null) validate(paging.equals(Paging.NO_PAGING));
		if (!paging.equals(Paging.NO_PAGING)) validate(jobResultId == null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public List<ImportJobResult> jobResults;
	}
}
