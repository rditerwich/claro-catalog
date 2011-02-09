package claro.catalog.manager.client.orders;

import static claro.catalog.manager.client.orders.OrderUtil.formatOrderDate;
import static claro.catalog.manager.client.orders.OrderUtil.formatOrderDateLong;
import static claro.catalog.manager.client.orders.OrderUtil.formatStatus;

import java.util.Date;

import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.order.OrderStatus;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

import easyenterprise.lib.gwt.client.widgets.Header;
import easyenterprise.lib.gwt.client.widgets.MoneyWidget;
import easyenterprise.lib.util.Money;

public class OrderDetail extends Composite implements Globals {
	
	private final OrderModel model;
	
	private Header header;
	private DateBox dateBox;
	private TextBox nameTextBox;
	private TextBox userTextBox;
	private ListBox orderStatusBox;
	private MoneyWidget amountPaidBox;


	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(images.removeIcon()) {
		protected String getMessage() {
			if (model != null && model.getOrder() != null) {
				return messages.removeOrderConfirmationMessage(formatOrderDate(model.getOrder().getOrderDate())); // TODO How to identify an order?
			} else {
				return "";
			}
		};
		protected void yesPressed() {
//			StoreOrder command = new StoreOrder(model.getOrder());
//			command.removeOrder = true;
//			model.store(command);
//			model.renderAll();
		}
	};
	


	public OrderDetail(OrderModel model_) {
		this.model = model_;
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("OrderDetail");
			add(new Grid(1, 2) {{
				setWidget(0, 0, header = new Header(1, "") {{
				}});
				setWidget(0, 1, new Anchor(messages.removeOrderLink()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							removeWithConfirmation.show();
						}
					});
				}});
			}});
			add(new FormTable() {{
				add(messages.orderDateLabel(), dateBox = new DateBox() {{
					// TODO Set format
				}});
				add(messages.orderShopNameLabel(), nameTextBox = new TextBox());
				add(messages.orderUserLabel(), userTextBox = new TextBox());
				add(messages.orderStatusLabel(), orderStatusBox = new ListBox() {{
					for (OrderStatus status : OrderStatus.values()) {
						addItem(formatStatus(status), status.name());
					}
				}});
				add(messages.orderAmountPaidLabel(), amountPaidBox = new MoneyWidget() {
					protected void valueChanged(Money newValue) {
						doStoreOrder();
					}
				});
				// TODO add delivery address
				// TODO add transport.
			}});		
		}});
		
		ValueChangeHandler<?> changeHandler = new ValueChangeHandler<Object>() {
			public void onValueChange(ValueChangeEvent<Object> event) {
				doStoreOrder();
			}
		};
		dateBox.addValueChangeHandler((ValueChangeHandler<Date>) changeHandler);
		nameTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		userTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		orderStatusBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				doStoreOrder();
			}
		});
	}

	public void render() {
		removeWithConfirmation.hide();
		header.setText(formatOrderDateLong(model.getOrder().getOrderDate()));
		
		dateBox.setValue(model.getOrder().getOrderDate());
		nameTextBox.setText(model.getOrder().getShop().getName());
		userTextBox.setText(model.getOrder().getUser() != null? model.getOrder().getUser().getEmail() : ""); // TODO 
		orderStatusBox.setSelectedIndex(findStatusIndex(model.getOrder().getStatus()));
	}
	

	private int findStatusIndex(OrderStatus status) {
		if (status == null) {
			return -1;
		}

		int i = 0;
		for (OrderStatus candidate : OrderStatus.values()) {
			if (candidate == status) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private void doStoreOrder() {
//		model.getOrder().setName(nameTextBox.getText());
//		model.getOrder().setUrlPrefix(urlPrefixTextBox.getText());
//		model.getOrder().setDefaultLanguage(getSelectedDefaultLanguage());
//		model.getOrder().setLanguages(CatalogModelUtil.mergeLanguages(languagesWidget.getLanguages()));
//		
//		model.store(new StoreOrder(model.getOrder()));
	}
}

