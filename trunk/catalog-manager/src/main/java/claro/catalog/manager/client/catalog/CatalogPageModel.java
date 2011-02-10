package claro.catalog.manager.client.catalog;

import claro.catalog.manager.client.items.ItemPageModel;
import claro.jpa.shop.Shop;

public class CatalogPageModel extends ItemPageModel {

	private Long selectedProductId;

	public void setSelectedProductId(Long productId) {
		this.selectedProductId = productId;
	}

	public Long getSelectedProductId() {
		return selectedProductId;
	}
}
