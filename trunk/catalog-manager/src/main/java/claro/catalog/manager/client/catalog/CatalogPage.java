package claro.catalog.manager.client.catalog;


import java.util.Collections;

import claro.catalog.command.RootDataCommand;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.FindItems.ResultType;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.StoreProduct;
import claro.catalog.command.items.StoreProduct.Result;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.widgets.StatusMessage;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;

public class CatalogPage extends Page {

	private LayoutPanel mainPanel;
	private ProductMasterDetail productMasterDetail;

	private boolean initialized;
	
	private Long currentCatalogId;
	private PropertyInfo nameProperty;
	
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
	}
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}

		initialized = true;
		
		mainPanel.add(productMasterDetail = new ProductMasterDetail() {
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
		
		// TODO update OuputChannels and Languages. 
		
	}

	private void updateProductListRootProperties() {
		RootDataCommand cmd = new RootDataCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<RootDataCommand.Result>() {

			public void onSuccess(RootDataCommand.Result result) {
				nameProperty = result.rootProperties.get(RootProperties.NAME);
				productMasterDetail.setRootProperties(result.rootProperties, result.generalGroup, result.rootCategory, result.rootCategoryLabels);
				updateProductList();
			}
		});
	}
	
	private void updateProductList() {
		FindItems cmd = new FindItems();
		cmd.catalogId = currentCatalogId;
		cmd.resultType = ResultType.products;
		
		cmd.outputChannelId = productMasterDetail.getOutputChannel() != null? productMasterDetail.getOutputChannel().getId() : null;
		cmd.language = productMasterDetail.getLanguage();
		
		cmd.filter = productMasterDetail.getFilter();
		cmd.categoryIds = productMasterDetail.getFilterCategories().getKeys();
		cmd.orderByIds = Collections.singletonList(nameProperty.propertyId); 

		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<FindItems.Result>(messages.loadingProducts()) {
			public void onSuccess(FindItems.Result result) {
				productMasterDetail.setProducts(result.items);
			}
		});
	}

	private void updateProductSelection(final Long productId) {
		final StatusMessage loadingMessage = StatusMessage.show(messages.loadingProductDetails(), 2, 1000);

		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.catalogId = currentCatalogId;
		cmd.itemId = productId;
		cmd.outputChannelId = productMasterDetail.getOutputChannel() != null? productMasterDetail.getOutputChannel().getId() : null;
		cmd.language = productMasterDetail.getLanguage();
		
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<ItemDetailsCommand.Result>() {
			public void onSuccess(ItemDetailsCommand.Result result) {
				loadingMessage.cancel();
				productMasterDetail.setSelectedProduct(productId, result.categories, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}
	
	private void storeItem(final StoreProduct cmd) {
		final StatusMessage savingMessage = StatusMessage.show(messages.savingProductDetailsStatus(), 2, 1000);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = productMasterDetail.getOutputChannel() != null? productMasterDetail.getOutputChannel().getId() : null;
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreProduct.Result>() {
			public void onFailure(Throwable caught) {
				savingMessage.cancel();
				StatusMessage.showError(messages.savingProductDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
				savingMessage.cancel();
				StatusMessage.show(messages.savingProductDetailsSuccessStatus());
				
				productMasterDetail.updateProduct(cmd.productId, result.storedProductId, result.masterValues, result.categories, result.detailValues, true);
			}
		});
	}
}
