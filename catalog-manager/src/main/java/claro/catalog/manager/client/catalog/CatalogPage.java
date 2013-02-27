package claro.catalog.manager.client.catalog;

import static easyenterprise.lib.gwt.client.StyleUtil.addStyle;

import java.util.Collections;

import claro.catalog.command.RootDataCommand;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.command.items.StoreItemDetails.Result;
import claro.catalog.data.ItemType;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.webshop.ShopModel;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;
import claro.catalog.manager.client.widgets.StatusMessage;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.PagedData;
import easyenterprise.lib.gwt.client.PagedData.Listener;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.SMap;

public class CatalogPage extends Page implements PagedData.DataSource<Long, SMap<PropertyInfo, SMap<String, Object>>> {

	public static enum Styles implements Style { productMasterDetail, productprice, productname, product, productTD, productpanel }

	private PagedData<Long, SMap<PropertyInfo, SMap<String, Object>>> productData = new PagedData<Long, SMap<PropertyInfo, SMap<String, Object>>>(20, this, new Listener() {
		public void dataChanged() {
//		  int index = productData.indexOnPage(model.getSelectedProductId());
//		  if (index >= 0) {
//		    SMap<PropertyInfo, SMap<String, Object>> data = productData.get(index);
//		    model.productDataChanged(data);
//		  }
			render();
		}
	});
	

	private CatalogPageModel model = new CatalogPageModel() {
		public PagedData<Long, SMap<PropertyInfo, SMap<String, Object>>> getProductData() {
			return productData;
		}
		public ShopModel getShopModel() {
			return shopModel;
		}
	};
	private MasterDetail productMasterDetail;

	private PropertyInfo nameProperty;
	private ShopModel shopModel;
	CatalogRibbon ribbon;
	private CatalogPageMaster productsMaster;
	private ProductDetails productDetails;
	
	public CatalogPage(PlaceController placeController) {
		super(placeController);
	}
	

	public void setShopModel(ShopModel shopModel) {
		this.shopModel = shopModel;
	}


	public void show() {
		updateProductList();
	}
	
	@Override
	protected void initialize() {
		initWidget(new LayoutPanel() {{
			add(productMasterDetail = new CatalogManagerMasterDetail(150) {{
				setRowChangedHandler(new ValueChangeHandler<Integer>() {
					public void onValueChange(ValueChangeEvent<Integer> event) {
						updateProductSelection(model.getProductData().getKey(getCurrentRow()));
					}
				});
			}
			public void onResize() {
				super.onResize();
				render();
			};
			});
		}});
		
		ribbon = new CatalogRibbon(this, model);
		productsMaster = new CatalogPageMaster(this, model);
		productDetails = new ProductDetails(this, model);
		
		addStyle(productMasterDetail, Styles.productMasterDetail);
		productMasterDetail.setHeader(ribbon);

		// Read Root properties
		updateProductListRootProperties();		
	}

	public void render() {
		ribbon.render();
//		if (ribbon.filterRibbon.isCurrent()) {
		productMasterDetail.setMaster(productsMaster);
		productMasterDetail.setDetail(productDetails);
		productsMaster.render();
		productDetails.render();
		if (model.getSelectedProductId() != null) {
			int row = productData.indexOnPage(model.getSelectedProductId());
			productMasterDetail.setCurrentRow(row);
		}
//		}
	}
	
	public void updateProductList() {
		productData.flush();
	}
	
	public void fetchData(Paging page, final PagedData.Callback<Long, SMap<PropertyInfo, SMap<String, Object>>> callback) {
		FindItems cmd = new FindItems();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.resultType = ItemType.product;
		
		cmd.outputChannelId = model.getSelectedShopId();
		cmd.language = model.getSelectedLanguage();
		
		cmd.filter = model.getFilterString();
		cmd.categoryIds = model.getFilterCategories().getKeys();
		cmd.orderByIds = model.getOrderByIds(); 
		cmd.paging = page;

		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<FindItems.Result>(messages.loadingProducts()) {
			public void onSuccess(FindItems.Result result) {
				// TODO Compare with remembered command!!
				super.onSuccess(result);
				callback.dataFetched(CollectionUtil.asList(result.items));
			}
		});
	}


	public void updateProductListRootProperties() {
		RootDataCommand cmd = new RootDataCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<RootDataCommand.Result>() {

			public void onSuccess(RootDataCommand.Result result) {
				nameProperty = result.rootProperties.get(RootProperties.NAME);
				model.setRootCategory(result.rootCategory);
				model.setRootProperties(result.rootProperties);
				updateProductList();
			}
		});
	}

	public void createNewProduct(final Long parentId) {
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
				
				model.setSelectedProductId(result.storedItemId);
				model.setProductDataForCreate(result.storedItemId, result.masterValues, result.parentExtentWithSelf, result.parents, result.propertyData, result.promotions);
			}
		});
	}

	public void updateProductSelection(final Long productId) {
		final StatusMessage loadingMessage = StatusMessage.show(messages.loadingProductDetails(), 2, 1000);

		final ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.itemId = productId;
		cmd.outputChannelId = model.getSelectedShopId();
		cmd.language = model.getSelectedLanguage();
		
		GwtCommandFacade.execute(cmd, new StatusCallback<ItemDetailsCommand.Result>() {
			public void onSuccess(ItemDetailsCommand.Result result) {
				loadingMessage.cancel();
				super.onSuccess(result);

				model.setSelectedProductId(cmd.itemId);
				model.setProductData(productId, result.masterValues, result.parentExtentWithSelf, result.parents, result.propertyData, result.promotions);
			}
		});
		
		// TODO See whether it was cached 
	}
	void storeItem(final StoreItemDetails cmd) {
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
				
				if (cmd.remove) {
					model.removeProduct(cmd.itemId);
					// TODO Who do we need to invalidate?? E.g.,  webshops (promotions) ??
				} else {
					model.setProductData(result.storedItemId, result.masterValues, result.parentExtentWithSelf, result.parents, result.propertyData, result.promotions);
				}
			}
		});
	}
}
