package claro.catalog.command.shop;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;

import java.util.List;

import claro.catalog.command.shop.StoreShop.Result;
import claro.jpa.catalog.Template;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.Shop;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class StoreShop implements Command<Result> {

	private static final long serialVersionUID = 1L;

	protected StoreShop() {
	}
	
	public StoreShop(Shop shop) {
		this.shop = shop;
	}
	
	/**
	 * Only necessary for new shops
	 */
	public Long catalogId;
	
	/**
	 * Import source to be stored, recursively. Deleted children are not detected, please
	 * use ...ToBeRemoved collections
	 */
	public Shop shop;
	
	/**
	 * Remove the entire shop. The field {@link #skipShop}
	 * should be set to false, {@link #importCategoriesToBeRemoved} and
	 * {@link #importPropertiesToBeRemoved} should be empty.
	 */
	public boolean removeShop;
	public List<Navigation> navigationsToBeRemoved;
	public List<Promotion> promotionsToBeRemoved;
	public List<Template> templatesToBeRemoved;
	
	public void checkValid() throws CommandValidationException {
		validate (shop != null, "No shop specified");
		validate (shop.getId() != null || catalogId != null, "CatalogId is required for new shops");
		if (removeShop) validate(isEmpty(navigationsToBeRemoved), "Nested removes are redundant");
		if (removeShop) validate(isEmpty(promotionsToBeRemoved), "Nested removes are redundant");
		if (removeShop) validate(isEmpty(templatesToBeRemoved), "Nested removes are redundant");
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public Shop shop;
	}
}
