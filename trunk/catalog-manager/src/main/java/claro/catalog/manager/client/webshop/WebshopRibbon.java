package claro.catalog.manager.client.webshop;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;

public class WebshopRibbon extends Composite implements Globals {

	private final ShopModel model;

	public WebshopRibbon(final ShopModel model) {
		this.model = model;
		initWidget(new Anchor(messages.newWebshopLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					model.createWebshop();
					event.stopPropagation();
				}
			});
		}});
	}
}
