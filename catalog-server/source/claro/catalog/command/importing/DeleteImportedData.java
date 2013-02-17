package claro.catalog.command.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class DeleteImportedData implements Command<DeleteImportedData.Result> {

	private static final long serialVersionUID = 1L;
	
	public Long importSourceId = null;
	
	protected DeleteImportedData() {
	}
	
	public DeleteImportedData(ImportSource importSource) {
		this.importSourceId = importSource.getId();
	}
	
	public void checkValid() throws CommandValidationException {
		validate(importSourceId != null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
	}
}
