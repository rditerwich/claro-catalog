package claro.catalog.command.shop;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import claro.jpa.shop.Shop;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Paging;

public class GetShops implements Command<GetShops.Result> {

	private static final long serialVersionUID = 1L;

	public Long catalogId;
	
	public Paging paging = Paging.NO_PAGING;
	
	public void checkValid() throws CommandValidationException {
		validate(catalogId != null);
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		
		public List<Shop> shops;
	}
}
