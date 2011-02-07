package claro.catalog.impl.shop;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import claro.catalog.command.shop.StoreShop;
import claro.jpa.catalog.Language;
import claro.jpa.shop.Shop;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;

public class StoreShopImpl extends StoreShop implements CommandImpl<StoreShop.Result> {
	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		EntityManager em = JpaService.getEntityManager();
		validateCommand(em);
		
		Result result = new Result();
		if (removeShop) {
			if (shop.getId() != null) {
				shop = em.find(Shop.class, shop.getId());
				em.remove(shop);
			}
		} else {
			
			// Check languages before merging
			XXX
			List<Language> languages = new ArrayList();
			for (Language language : result.shop.getLanguages()) {
				languages.add(em.find(Language.class, language));
			}
			result.shop.setLanguages(languages);

			// store changes to shop
			result.shop = em.merge(shop);
		}

		return null;
	}

	private void validateCommand(EntityManager em) throws CommandValidationException {
		checkValid();
		
	}
}
