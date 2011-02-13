package claro.catalog.impl.shop;


import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogModelService;
import claro.catalog.command.shop.StoreShop;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Template;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.Shop;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.CollectionUtil;

public class StoreShopImpl extends StoreShop implements CommandImpl<StoreShop.Result> {
	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		CatalogModel model = CatalogModelService.getCatalogModel(catalogId);
		CatalogDao dao = model.dao;
		EntityManager em = dao.getEntityManager();
		validateCommand(em);

		Result result = new Result();
		if (removeShop) {
			if (shop.getId() != null) {
				shop = em.find(Shop.class, shop.getId());
				em.remove(shop);
			}
		} else {
			
			// store changes to shop
			result.shop = em.merge(shop);
			
			// Update catalog opposite
			result.shop.getCatalog().getOutputChannels().remove(result.shop);
			result.shop.getCatalog().getOutputChannels().add(result.shop);

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
			
			// 
			if (topLevelCategoryIds != null) {
				em.refresh(result.shop);
				List<Navigation> navs = result.shop.getNavigation();
				while (navs.size() < topLevelCategoryIds.size()) {
					navs.add(new Navigation());
				}
				while (navs.size() > topLevelCategoryIds.size()) {
					Navigation nav = navs.remove(navs.size() - 1);
					em.remove(nav);
				}
				for (int i = navs.size() - 1; i >= 0; i--) {
					Navigation navigation = navs.get(i);
					Category category = em.find(Category.class, topLevelCategoryIds.get(i));
					navigation.setParentShop(result.shop);
					navigation.setCategory(category);
				}
			}
			
			// clone result
			result.shop = Cloner.clone(result.shop, GetShopsImpl.view);
		}


		// Flush cache
		model.flushCache();
		
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
		}
	}
}
