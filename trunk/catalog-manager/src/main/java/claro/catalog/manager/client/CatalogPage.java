package claro.catalog.manager.client;


import claro.catalog.command.RootPropertiesCommand;
import claro.catalog.command.RootPropertiesCommandResult;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.ItemDetailsCommandResult;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.catalog.Item;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.SMap;

public class CatalogPage extends Page {

	private LayoutPanel mainPanel;
	private ProductMasterDetail filteredProductList;

	private Item selectedItem;
	
	private boolean initialized;
	
	private Long currentCatalogId;
	private Long currentOuputChannel;
	private String currentLanguage;

	
	
	public CatalogPage(PlaceController placeController) {
		super(placeController);
		
		currentCatalogId = -1L; // TODO Make dynamic.
		initWidget(mainPanel = new LayoutPanel());
	}

	@Override
	public Place getPlace() {
		// TODO implement
		return null;
	}

	public void show() {
		initializeMainPanel();
		
		// Retrieve Products for filter:
		updateProductList();

	}
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}

		initialized = true;
		
		mainPanel.add(filteredProductList = new ProductMasterDetail() {
			protected void productSelected(final Long productId) {
				updateProductSelection(productId);
			}
			protected void updateProductList() {
				CatalogPage.this.updateProductList();
			}
		});
		
		// Read Root properties
		updateProductListRootProperties();		
		
		// TODO update PL categories
		// TODO update OuputChannels and Languages. 
		
		// TODO Listen to language selection??
	}

	private void updateProductListRootProperties() {
		RootPropertiesCommand cmd = new RootPropertiesCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<RootPropertiesCommandResult>() {
			public void onSuccess(RootPropertiesCommandResult result) {
				filteredProductList.setRootProperties(result.rootProperties);
			}
		});
	}
	
	private void updateProductList() {
		FindItems cmd = new FindItems();
		cmd.catalogId = currentCatalogId;
		
		cmd.outputChannelId = currentOuputChannel;
		cmd.language = currentLanguage;
		cmd.filter = filteredProductList.getFilter();
		cmd.categoryIds = filteredProductList.getFilterCategories().getKeys();
		// TODO set more command pars.

		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<FindItems.Result>(messages.loadingProducts()) {
			public void onSuccess(FindItems.Result result) {
				filteredProductList.setProducts(result.items);
			}
		});
	}

	private void updateProductSelection(final Long productId) {
		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd .setCatalogId(currentCatalogId)
			.setItem(selectedItem.getId());
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<ItemDetailsCommandResult>() {
			public void onSuccess(ItemDetailsCommandResult result) {
				filteredProductList.setSelectedProduct(productId, result.categories, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}
}
