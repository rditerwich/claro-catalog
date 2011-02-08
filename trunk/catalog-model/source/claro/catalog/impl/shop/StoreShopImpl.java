package claro.catalog.impl.shop;


import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDao;
import claro.catalog.command.shop.StoreShop;
import claro.jpa.catalog.Language;
import claro.jpa.catalog.Template;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.Shop;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.CollectionUtil;

public class StoreShopImpl extends StoreShop implements CommandImpl<StoreShop.Result> {
	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		EntityManager em = JpaService.getEntityManager();
		validateCommand(em);
		CatalogDao dao = new CatalogDao(em);

		Result result = new Result();
		if (removeShop) {
			if (shop.getId() != null) {
				shop = em.find(Shop.class, shop.getId());
				em.remove(shop);
			}
		} else {
			
			// Check languages before merging
			List<Language> languages = new ArrayList<Language>();
			for (Language language : CollectionUtil.notNull(shop.getLanguages())) {
				Language languageEntity = dao.findLanguageByName(language.getName());
				if (languageEntity != null) {
					languages.add(languageEntity);
				}
			}
			shop.setLanguages(languages);

			// store changes to shop
			result.shop = em.merge(shop);

			// Remove composites
			for (Navigation n : CollectionUtil.notNull(navigationsToBeRemoved)) {
				// TODO
			}
			
			for (Promotion p : CollectionUtil.notNull(promotionsToBeRemoved)) {
				// TODO
			}
			
			for (Template t : CollectionUtil.notNull(templatesToBeRemoved)) {
				// TODO
			}
			
			
			// clone result
			result.shop = Cloner.clone(result.shop, GetShopsImpl.view);
		}

		return result;
	}

	private void validateCommand(EntityManager em) throws CommandValidationException {
		checkValid();
		
		// to be removeds must exist and be part of shop
		Shop attachedShop = null;
		if (shop.getId() != null) {
			attachedShop = em.find(Shop.class, shop.getId());
		}
		
		for (Navigation n : CollectionUtil.notNull(navigationsToBeRemoved)) {
			Navigation attachedNavigation = em.find(Navigation.class, n.getId());

			validate(attachedNavigation != null);
			validate(attachedShop.getNavigation().contains(attachedNavigation));
		}
		
		for (Promotion p : CollectionUtil.notNull(promotionsToBeRemoved)) {
			Promotion attachedPromotion = em.find(Promotion.class, p.getId());
			
			validate(attachedPromotion != null);
			validate(attachedShop.getPromotions().contains(attachedPromotion));
		}
		
		for (Template t : CollectionUtil.notNull(templatesToBeRemoved)) {
			Template attachedTemplate = em.find(Template.class, t.getId());
			validate(attachedTemplate != null);
			validate(attachedShop.getTemplates().contains(attachedTemplate));
		}
	}
}
