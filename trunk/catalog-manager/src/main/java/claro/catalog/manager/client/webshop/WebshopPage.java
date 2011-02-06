package claro.catalog.manager.client.webshop;

import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;

public class WebshopPage extends Page {

	private MasterDetail masterDetail;
	private WebshopRibbon ribbon;
	private WebshopMaster master;
	private PullUpTabs tabs;
	private WebshopDetail detail;
	private ShopModel model = new ShopModel() {

		public void setShop(claro.jpa.shop.Shop shop) {
		};
		
		protected void showRow(int row) {
			masterDetail.openDetail(row);
		};

		@Override
		protected void renderAll() {
			masterDetail.setCurrentRow(master.findObject(getShop()));
			master.render();
			detail.render();
		}
	};
	
	public WebshopPage(PlaceController placeController, String string) {
		super(placeController);
//		mainPanel = new LayoutPanel();
//		mainPanel.setStylePrimaryName("ImportPage");
//		initWidget(mainPanel);
	}

	@Override
	public void show() {
		model.fetchShops();
	}

	@Override
	protected void initialize() {
		initWidget(new LayoutPanel() {{
			add(masterDetail = new CatalogManagerMasterDetail(100) {{
				setHeader(ribbon = new WebshopRibbon(model));
				setMaster(master = new WebshopMaster(model));
				setDetail(tabs = new PullUpTabs(26, 5) {{
					setMainWidget(detail = new WebshopDetail(model));
//					addTab(new EEButton(messages.campaignsTab()), 100, campaignsPanel = new WebshopCampaignsPanel(model));
				}});
			}});
		}});
	}
}

