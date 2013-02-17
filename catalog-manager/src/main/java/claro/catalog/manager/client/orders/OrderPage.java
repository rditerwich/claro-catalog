package claro.catalog.manager.client.orders;

import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;
import claro.jpa.order.Order;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;

public class OrderPage extends Page {

	private MasterDetail masterDetail;
	private PullUpTabs tabs;

	private OrderRibbon ribbon;
	private OrderMaster master;
	private OrderDetail detail;

	private VerticalPanel orderProductOrderPanel;
	private VerticalPanel orderHistoryPanel;
	
	private OrderModel model = new OrderModel() {

		
		public void setOrder(Order shop) {
			super.setOrder(shop);
		};
		
		@Override
		protected void renderAll() {
			if (getOrder() != null) {
				master.render();
				detail.render();
				masterDetail.setCurrentRow(master.findObject(getOrder()));
			} else {
				master.render();
				masterDetail.closeDetail(false);
			}
		}

		@Override
		protected void openDetail() {
			masterDetail.openDetail();
		}
	};
	
	public OrderPage(PlaceController placeController, String string) {
		super(placeController);
//		mainPanel = new LayoutPanel();
//		mainPanel.setStylePrimaryName("ImportPage");
//		initWidget(mainPanel);
	}

	@Override
	public void show() {
		// TODO: Do not refresh data every time this page is shown?
		model.fetchOrders();
	}

	@Override
	protected void initialize() {
		initWidget(new LayoutPanel() {{
			add(masterDetail = new CatalogManagerMasterDetail(100) {{
				setHeader(ribbon = new OrderRibbon(model));
				setMaster(master = new OrderMaster(model));
				setDetail(tabs = new PullUpTabs(26, 5) {{
					setMainWidget(detail = new OrderDetail(model));
					addTab(new EEButton(messages.orderProductOrderTab()), 160, orderProductOrderPanel = new VerticalPanel());
					addTab(new EEButton(messages.orderHistoryTab()), 160, orderHistoryPanel = new VerticalPanel());
				}});
				setRowChangedHandler(new ValueChangeHandler<Integer>() {
					public void onValueChange(ValueChangeEvent<Integer> event) {
						model.setOrder(model.getOrders().get(masterDetail.getCurrentRow()));
					}
				});

			}});
		}});
		
	}
}

