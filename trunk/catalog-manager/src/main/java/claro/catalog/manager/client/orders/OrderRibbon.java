package claro.catalog.manager.client.orders;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

public class OrderRibbon extends Composite implements Globals {

	public OrderRibbon(final OrderModel model) {
		initWidget(new Grid(1, 2) {{
			setWidget(0, 0, new Anchor(messages.newOrderLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						model.createOrder();
						event.stopPropagation();
					}
				});
			}});
			setWidget(0, 1, new Anchor(messages.refresh()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						model.fetchOrders();
						event.stopPropagation();
					}
				});
			}});
			
		}});
	}
}
