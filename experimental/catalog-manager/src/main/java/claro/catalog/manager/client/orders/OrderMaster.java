package claro.catalog.manager.client.orders;

import static claro.catalog.manager.client.orders.OrderUtil.formatOrderDate;
import static claro.catalog.manager.client.orders.OrderUtil.formatStatus;

import claro.catalog.manager.client.Globals;
import claro.jpa.order.Order;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;

import easyenterprise.lib.gwt.client.widgets.TableWithObjects;

public class OrderMaster extends TableWithObjects<Order> implements Globals {

	private static final int SHOP_NAME_COL = 0;
	private static final int DATE_COL = 1;
	private static final int STATUS_COL = 2;
	private static final int NR_COLS = 3;

	private final OrderModel model;

	public OrderMaster(OrderModel model) {
		this.model = model;
		resizeColumns(NR_COLS);
		setHeaderText(0, SHOP_NAME_COL, messages.orderShopNameLabel());
		setHeaderText(0, DATE_COL, messages.orderDateLabel());
		setHeaderText(0, STATUS_COL, messages.orderStatusLabel());
	}
	
	public void render() {
		int fromRows = getRowCount();
		resizeRows(model.getOrders().size());
		
		// Create new Rows
		for (int row = fromRows; row < model.getOrders().size(); row++) {
			setObject(row, model.getOrders().get(row));
			
			final ClickHandler selectRowClickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
				}
			};
			
			setWidget(row, SHOP_NAME_COL, new Anchor() {{
				addClickHandler(selectRowClickHandler);
			}});
			setWidget(row, DATE_COL, new Label() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
			setWidget(row, STATUS_COL, new Label() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
		}
		
		// render all rows
		for (int row = 0; row < model.getOrders().size(); row++) {
			renderRow(row);
		}
		
	}
	
	public void renderRow(int row) {
		Order order = model.getOrders().get(row);
		setObject(row, order);
		((HasText) getWidget(row, SHOP_NAME_COL)).setText(order.getShop().getName());
		((HasText) getWidget(row, DATE_COL)).setText(formatOrderDate(order.getOrderDate()));
		((HasText) getWidget(row, STATUS_COL)).setText(formatStatus(order.getStatus()));
	}
}

