package claro.catalog.impl.shop;


import static easyenterprise.lib.command.CommandValidationException.validate;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogModelService;
import claro.catalog.command.shop.StorePromotion;
import claro.catalog.impl.items.ItemUtil;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.Product;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.VolumeDiscountPromotion;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class StorePromotionImpl extends StorePromotion implements CommandImpl<StorePromotion.Result> {
	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		CatalogModel model = CatalogModelService.getCatalogModel(catalogId);
		CatalogDao dao = model.dao;
		EntityManager em = dao.getEntityManager();
		validateCommand(em);

		Result result = new Result();
		if (removePromotion) {
			if (promotion.getId() != null) {
				promotion = em.find(Promotion.class, promotion.getId());
				// TODO any nullifying?
				em.remove(promotion);
			}
		} else {
			if (productId != null && promotion instanceof VolumeDiscountPromotion) {
				VolumeDiscountPromotion volumePromotion = (VolumeDiscountPromotion) promotion;
				volumePromotion.setProduct(em.find(Product.class, productId));
				result.additionalReferredProducts = SMap.create(productId, ItemUtil.getNameLabels(model.getItem(productId), model, promotion.getShop(), null));
			}
			
			// store changes to shop
			result.promotion = em.merge(promotion);
			
			// Update opposites
			result.promotion.getShop().getPromotions().remove(result.promotion);
			result.promotion.getShop().getPromotions().add(result.promotion);

			// clone result
			result.promotion = Cloner.clone(result.promotion, FindPromotionsImpl.view);
		}

		return result;
	}

	private void validateCommand(EntityManager em) throws CommandValidationException {
		checkValid();
		
		validate(!removePromotion || promotion.getId() != null);
		if (removePromotion && promotion.getId() != null) {
			validate(em.find(Promotion.class, promotion.getId()) != null);
		}
	}
}
