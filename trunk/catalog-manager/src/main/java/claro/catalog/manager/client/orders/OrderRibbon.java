package claro.catalog.manager.client.orders;

import static claro.catalog.manager.client.orders.OrderUtil.formatStatus;
import claro.catalog.manager.client.Globals;
import claro.jpa.order.OrderStatus;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;

public class OrderRibbon extends Composite implements Globals {
	private enum Styles implements Style { orderRibbon }

	public OrderRibbon(final OrderModel model) {
		initWidget(new Grid(2, 2) {{
			StyleUtil.addStyle(this, Styles.orderRibbon);
			setWidget(0, 0, new Label(messages.orderFilter()));
			setWidget(0, 1, new ListBox() {{
				addItem(""); // No selection
				for (OrderStatus status : OrderStatus.values()) {
					addItem(formatStatus(status), status.name());
				}
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						int selectedIndex = getSelectedIndex();
						OrderStatus filterStatus = null;
						if (selectedIndex > 0) {
							filterStatus = OrderStatus.valueOf(getValue(selectedIndex));
						}
						model.setStatusFilter(filterStatus);
						model.fetchOrders();
					}
				});
			}});
			setWidget(1, 0, new Anchor(messages.refresh()) {{
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
