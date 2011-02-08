package claro.catalog.command.items;

import static easyenterprise.lib.command.CommandValidationException.validate;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class PerformStaging implements Command<PerformStaging.Result> {

	private static final long serialVersionUID = 1L;

	public Long catalogId;

	/**
	 * When null, the no-staging values will be copied.
	 */
	public String fromStagingName;
	
	/**
	 * Must not be null.
	 */
	public String toStagingName;
	
	
	
	@Override
	public void checkValid() throws CommandValidationException {
		validate(catalogId != null, "no catalog specified");
		validate(toStagingName != null, "no to staging-area specified");
	}

	public class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
	}
}
