package claro.catalog.manager.client.webshop;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

public class WebshopRibbon extends Composite implements Globals {

	public WebshopRibbon(final ShopModel model) {
		initWidget(new Grid(1, 2) {{
			setWidget(0, 0, new Anchor(messages.newWebshopLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						model.createWebshop();
						event.stopPropagation();
					}
				});
			}});
			setWidget(0, 1, new Anchor(messages.refresh()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						model.fetchShops();
						event.stopPropagation();
					}
				});
			}});
			
		}});
	}
}
