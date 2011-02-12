package claro.catalog.manager.client.webshop;

import static easyenterprise.lib.util.ObjectUtil.orElse;
import claro.catalog.command.shop.StoreShop;
import claro.catalog.manager.client.widgets.FormTable;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.util.ObjectUtil;

public class WebshopShippingOptionsPanel extends Composite {

	private final ShopModel model;
	private TextBox shippingCostsTextBox;
	private TextBox expressDeliveryChargeTextBox;

	public WebshopShippingOptionsPanel(ShopModel model_) {
		this.model = model_;
		initWidget(new FormTable() {{
			add(messages.shippingCostsLabel(), shippingCostsTextBox = new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						model.getShop().setShippingCosts(Double.valueOf(getText()));
						model.store(new StoreShop(model.getShop()));
					}
				});
			}}, messages.shippingCostsHelp());
			add(messages.expressDeliveryChargeLabel(), expressDeliveryChargeTextBox = new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						model.getShop().setExpressDeliveryCharge(Double.valueOf(getText()));
						model.store(new StoreShop(model.getShop()));
					}
				});
			}}, messages.expressDeliveryChargeHelp());
		}});
	}
	
	public void render() {
		shippingCostsTextBox.setText(orElse(model.getShop().getShippingCosts(), 0).toString());
		expressDeliveryChargeTextBox.setText(orElse(model.getShop().getExpressDeliveryCharge(), 0).toString());
	}
}
