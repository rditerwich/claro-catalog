package claro.catalog.impl.shop;

import claro.catalog.CatalogModelService;
import claro.catalog.command.shop.FindPromotions;
import claro.catalog.impl.items.ItemUtil;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.VolumeDiscountPromotion;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class FindPromotionsImpl extends FindPromotions implements CommandImpl<FindPromotions.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView("shop", "product");

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogModel model = CatalogModelService.getCatalogModel(catalogId);
		
		
		result.promotions = model.dao.findPromotions(catalogId, shops);

		result.referredProducts = SMap.empty();
		for (Promotion promotion : result.promotions) {
			if (promotion instanceof VolumeDiscountPromotion) {
				VolumeDiscountPromotion volumePromotion = (VolumeDiscountPromotion) promotion;
				
				if (volumePromotion.getProduct() != null) {
					Long productId = volumePromotion.getProduct().getId();
					ItemModel productModel = model.getItem(productId);
	
					result.referredProducts = result.referredProducts.set(productId, ItemUtil.getNameLabels(productModel, model, promotion.getShop(), null));
				}
			}
		}
			
		result.promotions = Cloner.clone(result.promotions, view);
		return result;
	}

}
