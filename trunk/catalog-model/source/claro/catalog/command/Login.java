package claro.catalog.command;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class Login implements Command<Login.Result> {

	private static final long serialVersionUID = 1L;
	
	public String opendIdName;  // The id the user provides...

	@Override
	public void checkValid() throws CommandValidationException {
		// TODO Auto-generated method stub
	}

	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;

		public String redirectUrl;
	}
	
}
