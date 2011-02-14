package claro.catalog.manager.client.catalog;

import claro.catalog.manager.client.items.ItemPageModel;
import claro.catalog.manager.client.webshop.ShopModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.shop.Shop;
import easyenterprise.lib.util.SMap;

public abstract class CatalogPageModel extends ItemPageModel {

	private Long selectedProductId;
	private SMap<OutputChannel, SMap<Long, SMap<String, String>>> promotions;
	
	public void setSelectedProductId(Long productId) {
		this.selectedProductId = productId;
	}

	public Long getSelectedProductId() {
		return selectedProductId;
	}

	public void setPromotionsForSelectedProduct(SMap<OutputChannel, SMap<Long, SMap<String, String>>> promotions) {
		this.promotions = promotions;
	}
	
	public SMap<OutputChannel, SMap<Long, SMap<String, String>>> getPromotions() {
		return promotions;
	}

	protected abstract ShopModel getShopModel();
}
