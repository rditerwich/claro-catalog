package claro.catalog.command;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.jpa.shop.Shop;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;


public class GetLanguagesByShop implements Command<GetLanguagesByShop.Result> {
	private static final long serialVersionUID = 1L;

	public Long catalogId;
	
	@Override
	public void checkValid() throws CommandValidationException {
		validate (catalogId != null);
	}

	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;

		public SMap<Shop, String> languages;
	}
}
