package claro.catalog.command.shop;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.catalog.command.shop.StorePromotion.Result;
import claro.jpa.shop.Promotion;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class StorePromotion implements Command<Result> {

	private static final long serialVersionUID = 1L;

	protected StorePromotion() {
	}
	
	public StorePromotion(Promotion promotion) {
		this.promotion = promotion;
	}
	
	public Long catalogId;
	
	public Promotion promotion;
	
	public Long productId;  // TODO this is a quick hack and should be handeled nicer.
	
	public boolean removePromotion;
	
	public void checkValid() throws CommandValidationException {
		validate (promotion != null, "No promotion specified");
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public Promotion promotion;
		public SMap<Long, SMap<String, String>> additionalReferredProducts;
	}
}
