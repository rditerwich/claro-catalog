package claro.catalog.impl;

import claro.catalog.command.Login;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.gwt.server.OpenIdServlet;

public class LoginImpl extends Login implements CommandImpl<Login.Result>{
	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		
		Result result = new Result();
		
		result.redirectUrl = OpenIdServlet.getAuthenticationURL(opendIdName);
		
		return result;
	}

	
	
}
