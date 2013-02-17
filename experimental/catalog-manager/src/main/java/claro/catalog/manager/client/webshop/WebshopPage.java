package claro.catalog.manager.client.webshop;

import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;

public class WebshopPage extends Page {

	private MasterDetail masterDetail;
	private WebshopRibbon ribbon;
	private WebshopMaster master;
	private PullUpTabs tabs;
	private WebshopDetail detail;
	private WebshopShippingOptionsPanel shippingOptionsPanel;
	private WebshopPromotionsPanel promotionsPanel;
	private ShopModel model = new ShopModel() {

		public void setShop(claro.jpa.shop.Shop shop) {
			super.setShop(shop);
		};
		
		@Override
		protected void renderAll() {
			if (!initialized) { // TODO Is this a hack?
				showPage(WebshopPage.this);
			}
			if (getShop() != null) {
				master.render();
				detail.render();
				shippingOptionsPanel.render();
				if (getPromotions() != null) {
					promotionsPanel.render();
				}
				masterDetail.setCurrentRow(master.findObject(getShop()));
			} else {
				master.render();
				masterDetail.closeDetail(false);
			}
		}

		@Override
		protected void openDetail() {
			masterDetail.openDetail();
		}

		@Override
		protected void showPromotions() {
			tabs.showTab(promotionsPanel);
		}

		@Override
		protected void showWebshopPage() {
			showPage(WebshopPage.this);
		}
	};
	private boolean initialized;
	
	public WebshopPage(PlaceController placeController, String string) {
		super(placeController);
//		mainPanel = new LayoutPanel();
//		mainPanel.setStylePrimaryName("ImportPage");
//		initWidget(mainPanel);
	}
	
	
	public ShopModel getModel() {
		return model;
	}

	@Override
	public void show() {
		// TODO: Do not refresh data every time this page is shown?
		model.fetchShops();
	}

	@Override
	protected void initialize() {
		initialized = true;
		initWidget(new LayoutPanel() {{
			add(masterDetail = new CatalogManagerMasterDetail(100) {{
				setHeader(ribbon = new WebshopRibbon(model));
				setMaster(master = new WebshopMaster(model));
				setDetail(tabs = new PullUpTabs(26, 5) {{
					setMainWidget(detail = new WebshopDetail(model));
					addTab(new EEButton(messages.promotionsTab()), 120, promotionsPanel = new WebshopPromotionsPanel(model));
					addTab(new EEButton(messages.shippingOptionsTab()), 150, shippingOptionsPanel = new WebshopShippingOptionsPanel(model));
				}});
				setRowChangedHandler(new ValueChangeHandler<Integer>() {
					public void onValueChange(ValueChangeEvent<Integer> event) {
						model.setShop(model.getShops().get(masterDetail.getCurrentRow()));
					}
				});

			}});
		}});
		
	}
}

