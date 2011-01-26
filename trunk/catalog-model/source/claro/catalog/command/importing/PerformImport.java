package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import claro.jpa.jobs.JobResult;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class PerformImport implements Command<PerformImport.Result> {

	private static final long serialVersionUID = 1L;
	
	public Long catalogId;
	
	public Long importSourceId;
	
	/**
	 * Override import definition attributes, but instead read
	 * data from this url. Optional.
	 */
	public String importUrl = null;
	
	public String uploadFieldName = null;
	

	/**
	 * Overwrite 
	 */
	public boolean replaceExistingData = false;
	
	public boolean dryRun = false;

	public void checkValid() throws CommandValidationException {
		validate(catalogId != null, "no catalog specified");
		validate(importSourceId != null, "no import definition specified");
		validate(importUrl == null || uploadFieldName == null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public List<JobResult> jobResults;
	}
}
