package claro.catalog.manager.client.catalog;


import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.RootDataCommand;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.ItemType;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.command.items.StoreItemDetails.Result;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.widgets.StatusMessage;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.util.SMap;

public class CatalogPage extends Page {

	private CatalogPageModel model = new CatalogPageModel();
	private LayoutPanel mainPanel;
	private ProductMasterDetail productMasterDetail;

	private PropertyInfo nameProperty;
	
	public CatalogPage(PlaceController placeController) {
		super(placeController);
		
		initWidget(mainPanel = new LayoutPanel());
	}

	public void show() {
		if (productMasterDetail != null) {
			productMasterDetail.refreshLanguages();
		}
		updateProductList();
	}
	
	@Override
	protected void initialize() {
		mainPanel.add(productMasterDetail = new ProductMasterDetail(model) {
			protected void productSelected(final Long productId) {
				updateProductSelection(productId);
			}
			protected void updateProductList() {
				CatalogPage.this.updateProductList();
			}
			protected void createNewProduct(Long parentId) {
				CatalogPage.this.createNewProduct(parentId);
				
			}
			protected void storeItem(StoreItemDetails cmd) {
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
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.resultType = ItemType.product;
		
		cmd.outputChannelId = model.getSelectedShopId();
		cmd.language = model.getSelectedLanguage();
		
		cmd.filter = productMasterDetail.getFilter();
		cmd.categoryIds = productMasterDetail.getFilterCategories().getKeys();
		cmd.orderByIds = productMasterDetail.getOrderByIds(); 

		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<FindItems.Result>(messages.loadingProducts(), false) {
			public void onSuccess(FindItems.Result result) {
				super.onSuccess(result);
				productMasterDetail.setProducts(result.items);
			}
		});
	}

	private void createNewProduct(final Long parentId) {
		final StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemType = ItemType.product;
		cmd.valuesToSet = SMap.create(nameProperty, SMap.create(model.getSelectedLanguage(), (Object)messages.newProduct()));
		cmd.parentsToSet = Collections.singletonList(parentId);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = model.getSelectedShopId();
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreItemDetails.Result>() {
			public void onFailure(Throwable caught) {
//				savingMessage.cancel();
				StatusMessage.showError(messages.savingCategoryDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
//				savingMessage.cancel();
//				StatusMessage.show(messages.savingCategoryDetailsSuccessStatus());
				// TODO Proper status messages.
				
				productMasterDetail.productCreated(result.storedItemId, result.masterValues, result.parents, result.propertyData);
			}
		});

	}

	
	private void updateProductSelection(final Long productId) {
		final StatusMessage loadingMessage = StatusMessage.show(messages.loadingProductDetails(), 2, 1000);

		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.itemId = productId;
		cmd.outputChannelId = model.getSelectedShopId();
		cmd.language = model.getSelectedLanguage();
		
		GwtCommandFacade.execute(cmd, new StatusCallback<ItemDetailsCommand.Result>() {
			public void onSuccess(ItemDetailsCommand.Result result) {
				loadingMessage.cancel();
				super.onSuccess(result);

				for (Entry<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> group : result.propertyData) {
					for (Entry<PropertyInfo, PropertyData> propertyEntry : group.getValue()) {
						PropertyInfo property = propertyEntry.getKey();
//						alert("property + " + property.propertyId + " type " + property.type + " ord: " + property.type.ordinal());
//						alert("property + " + property.propertyId + " type " + property.getType() + " ord: " + property.getType().ordinal());
					}
				}

				
				productMasterDetail.setSelectedProduct(productId, result.parents, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}

	
	public static native void alert(String msg) /*-{
	  $wnd.alert(msg);
	}-*/;


	private void storeItem(final StoreItemDetails cmd) {
		final StatusMessage savingMessage = StatusMessage.show(messages.savingProductDetailsStatus(), 2, 1000);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = model.getSelectedShopId();
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreItemDetails.Result>() {
			public void onFailure(Throwable caught) {
				savingMessage.cancel();
				StatusMessage.showError(messages.savingProductDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
				savingMessage.cancel();
				StatusMessage.show(messages.savingProductDetailsSuccessStatus());
				
				productMasterDetail.updateProduct(cmd.itemId, result.storedItemId, result.masterValues, result.parents, result.propertyData);
			}
		});
	}
}
