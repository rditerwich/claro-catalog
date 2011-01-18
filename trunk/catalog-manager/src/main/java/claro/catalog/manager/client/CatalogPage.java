package claro.catalog.manager.client;


import claro.catalog.command.RootDataCommand;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.FindItems.ResultType;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.ItemDetailsCommandResult;
import claro.catalog.command.items.StoreProduct;
import claro.catalog.command.items.StoreProduct.Result;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.widgets.StatusMessage;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;

public class CatalogPage extends Page {

	private LayoutPanel mainPanel;
	private ProductMasterDetail filteredProductList;

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
			protected void storeItem(StoreProduct cmd) {
				CatalogPage.this.storeItem(cmd);
			}
		});
		
		// Read Root properties
		updateProductListRootProperties();		
		
		// TODO update PL categories
		// TODO update OuputChannels and Languages. 
		
		// TODO Listen to language selection??
	}

	private void updateProductListRootProperties() {
		RootDataCommand cmd = new RootDataCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<RootDataCommand.Result>() {
			public void onSuccess(RootDataCommand.Result result) {
				filteredProductList.setRootProperties(result.rootProperties, result.generalGroup, result.rootCategory, result.rootCategoryLabels);
			}
		});
	}
	
	private void updateProductList() {
		FindItems cmd = new FindItems();
		cmd.catalogId = currentCatalogId;
		cmd.resultType = ResultType.products;
		
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
		final StatusMessage loadingMessage = StatusMessage.show(messages.loadingProductDetails(), 2, 1000);

		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd .setCatalogId(currentCatalogId)
			.setItem(productId);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<ItemDetailsCommandResult>() {
			public void onSuccess(ItemDetailsCommandResult result) {
				loadingMessage.cancel();
				filteredProductList.setSelectedProduct(productId, result.categories, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}
	
	private void storeItem(final StoreProduct cmd) {
		final StatusMessage savingMessage = StatusMessage.show(messages.savingProductDetailsStatus(), 2, 1000);
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreProduct.Result>() {
			public void onFailure(Throwable caught) {
				savingMessage.cancel();
				StatusMessage.showError(messages.savingProductDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
				savingMessage.cancel();
				StatusMessage.show(messages.savingProductDetailsSuccessStatus());
				
				filteredProductList.updateProduct(cmd.productId, result.storedProductId, result.masterValues, result.categories, result.detailValues, true);
			}
		});
	}
}
