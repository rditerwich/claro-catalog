package claro.catalog.manager.client.webshop;

import claro.catalog.manager.client.widgets.FormTable;

import com.google.gwt.user.client.ui.Composite;

public class WebshopPromotionsPanel extends Composite {

	private final ShopModel model;

	public WebshopPromotionsPanel(ShopModel model_) {
		this.model = model_;
		initWidget(new FormTable() {{}});
	}

}
