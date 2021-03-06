package claro.catalog.manager.client.taxonomy;


import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.RootDataCommand;
import claro.catalog.command.items.GetItemTree;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.command.items.StoreItemDetails.Result;
import claro.catalog.data.ItemData;
import claro.catalog.data.ItemType;
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

public class TaxonomyPage extends Page {

	private TaxonomyModel model = new TaxonomyModel();
	private LayoutPanel mainPanel;
	private CategoryMasterDetail categoryMasterDetail;

	private PropertyInfo nameProperty;
	
	public TaxonomyPage(PlaceController placeController) {
		super(placeController);
		
		initWidget(mainPanel = new LayoutPanel());
	}

	public void show() {
		if (categoryMasterDetail != null) {
			categoryMasterDetail.refreshLanguages();
		}
		updateCategories();
	}
	
	@Override
	protected void initialize() {
		mainPanel.add(categoryMasterDetail = new CategoryMasterDetail(model) {
			protected void categorySelected(final Long categoryId) {
				updateCategorySelection(categoryId);
			}
			protected void updateCategories() {
				TaxonomyPage.this.updateCategories();
			}
			protected void storeItem(StoreItemDetails cmd) {
				TaxonomyPage.this.storeItem(cmd);
			}
			protected void createNewCategory(long parentId) {
				TaxonomyPage.this.createNewCategory(parentId);
			}
		});
		
		// Read Root properties
		updateRootProperties();		
	}

	private void updateRootProperties() {
		RootDataCommand cmd = new RootDataCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<RootDataCommand.Result>() {

			public void onSuccess(RootDataCommand.Result result) {
				nameProperty = result.rootProperties.get(RootProperties.NAME);
				categoryMasterDetail.setRootProperties(result.rootProperties, result.generalGroup, result.rootCategory, result.rootCategoryLabels);
				updateCategories();
			}
		});
	}
	
	private void updateCategories() {
		GetItemTree c = new GetItemTree();
		c.catalogId = CatalogManager.getCurrentCatalogId();
		c.outputChannelId = model.getSelectedOutputChannelId();

		invalidateCategoryTree(c.catalogId, c.outputChannelId);
		
		GwtCommandFacade.executeCached(c, 1000 * 60 * 60, new StatusCallback<GetItemTree.Result>() {
			public void onSuccess(GetItemTree.Result result) {
				categoryMasterDetail.setCategoriesTree(result.root, result.children, asDisplayNames(result.items));
			}
		});
	}

	private SMap<Long, SMap<String, String>> asDisplayNames(SMap<Long, ItemData> items) {
		SMap<Long, SMap<String, String>> result = SMap.empty();
		for (Entry<Long, ItemData> item : items) {
			result = result.add(item.getKey(), item.getValue().displayNames);
		}
		return result;
	}

	private void createNewCategory(final Long parentId) {
		final StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemType = ItemType.category;
		cmd.valuesToSet = SMap.create(nameProperty, SMap.create(model.getSelectedLanguage(), (Object)messages.newCategory()));
		cmd.parentsToSet = Collections.singletonList(parentId);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = model.getSelectedOutputChannelId();
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreItemDetails.Result>() {
			public void onFailure(Throwable caught) {
//				savingMessage.cancel();
				StatusMessage.showError(messages.savingCategoryDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
//				savingMessage.cancel();
//				StatusMessage.show(messages.savingCategoryDetailsSuccessStatus());
				// TODO Proper status messages.
				
				// Invalidate cached category tree.
				invalidateCategoryTree(cmd.catalogId, cmd.outputChannelId);
				
				// TODO Another option is to return the entire category tree in command and update the cache rather than invalidate it.
				categoryMasterDetail.categoryCreated(result.storedItemId, parentId, result.categoryLabels, result.groups, result.parentExtentWithSelf, result.parents, result.propertyData);
			}
		});

	}

	private void updateCategorySelection(final Long categoryId) {
		final StatusMessage loadingMessage = StatusMessage.show(messages.loadingCategoryDetails(), 2, 1000);

		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.itemId = categoryId;
		cmd.outputChannelId = model.getSelectedOutputChannelId();
		cmd.language = model.getSelectedLanguage();
		
		GwtCommandFacade.execute(cmd, new StatusCallback<ItemDetailsCommand.Result>() {
			public void onSuccess(ItemDetailsCommand.Result result) {
				loadingMessage.cancel();
				categoryMasterDetail.setSelectedCategory(categoryId, result.groups, result.parentExtentWithSelf, result.parents, result.propertyData);
			}
		});
		
	}
	
	private void storeItem(final StoreItemDetails cmd) {
		final StatusMessage savingMessage = StatusMessage.show(messages.savingCategoryDetailsStatus(), 2, 1000);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = model.getSelectedOutputChannelId();
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreItemDetails.Result>() {
			public void onFailure(Throwable caught) {
				savingMessage.cancel();
				StatusMessage.showError(messages.savingCategoryDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
				savingMessage.cancel();
				StatusMessage.show(messages.savingCategoryDetailsSuccessStatus());
				
				// Invalidate cached category tree.
				invalidateCategoryTree(cmd.catalogId, cmd.outputChannelId);
				
				// TODO Another option is to return the entire category tree in command and update the cache rather than invalidate it.
				categoryMasterDetail.updateCategory(cmd.itemId, result.storedItemId, null, result.categoryLabels, result.groups, result.parentExtentWithSelf, result.parents, result.propertyData);
			}
		});
	}

	private void invalidateCategoryTree(Long catalogId, Long outputChannelId) {
		GetItemTree getTreeCmd = new GetItemTree();
		getTreeCmd.catalogId = catalogId;
		getTreeCmd.outputChannelId = outputChannelId;
		getTreeCmd.stagingAreaId = null;
		
		GwtCommandFacade.invalidateCache(getTreeCmd);
	}
}
