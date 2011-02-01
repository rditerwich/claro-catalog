package claro.catalog.manager.client.taxonomy;


import java.util.Collections;

import claro.catalog.command.RootDataCommand;
import claro.catalog.command.items.GetCategoryTree;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.ItemType;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.command.items.StoreItemDetails.Result;
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
import easyenterprise.lib.util.SMap;

public class TaxonomyPage extends Page {

	private LayoutPanel mainPanel;
	private CategoryMasterDetail categoryMasterDetail;

	private boolean initialized;
	
	private PropertyInfo nameProperty;
	
	public TaxonomyPage(PlaceController placeController) {
		super(placeController);
		
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
		
		mainPanel.add(categoryMasterDetail = new CategoryMasterDetail() {
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
		
		// TODO update OuputChannels and Languages. 
		
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
		GetCategoryTree c = new GetCategoryTree();
		c.catalogId = CatalogManager.getCurrentCatalogId();
		c.outputChannelId = categoryMasterDetail.getOutputChannel() != null? categoryMasterDetail.getOutputChannel().getId() : null;

		GwtCommandFacade.executeCached(c, 1000 * 60 * 60, new StatusCallback<GetCategoryTree.Result>() {
			public void onSuccess(GetCategoryTree.Result result) {
				categoryMasterDetail.setCategoriesTree(result.root, result.children, result.categories);
			}
		});
	}

	private void createNewCategory(final Long parentId) {
		final StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemType = ItemType.catagory;
		cmd.valuesToSet = SMap.create(nameProperty, SMap.create(categoryMasterDetail.getLanguage(), (Object)messages.newCategory()));
		cmd.parentsToSet = Collections.singletonList(parentId);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = categoryMasterDetail.getOutputChannel() != null? categoryMasterDetail.getOutputChannel().getId() : null;
		
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
				GetCategoryTree getTreeCmd = new GetCategoryTree();
				getTreeCmd.catalogId = cmd.catalogId;
				getTreeCmd.outputChannelId = cmd.outputChannelId;
				getTreeCmd.stagingAreaId = cmd.stagingAreaId;
				
				GwtCommandFacade.invalidateCache(getTreeCmd);
				
				// TODO Another option is to return the entire category tree in command and update the cache rather than invalidate it.
				categoryMasterDetail.categoryCreated(result.storedItemId, parentId, result.categoryLabels, result.groups, result.parentExtentWithSelf, result.parents, result.propertyData);
			}
		});

	}

	private void updateCategorySelection(final Long productId) {
		final StatusMessage loadingMessage = StatusMessage.show(messages.loadingCategoryDetails(), 2, 1000);

		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.itemId = productId;
		cmd.outputChannelId = categoryMasterDetail.getOutputChannel() != null? categoryMasterDetail.getOutputChannel().getId() : null;
		cmd.language = categoryMasterDetail.getLanguage();
		
		GwtCommandFacade.execute(cmd, new StatusCallback<ItemDetailsCommand.Result>() {
			public void onSuccess(ItemDetailsCommand.Result result) {
				loadingMessage.cancel();
				categoryMasterDetail.setSelectedCategory(productId, result.groups, result.parentExtentWithSelf, result.parents, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}
	
	private void storeItem(final StoreItemDetails cmd) {
		final StatusMessage savingMessage = StatusMessage.show(messages.savingCategoryDetailsStatus(), 2, 1000);
		
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		cmd.outputChannelId = categoryMasterDetail.getOutputChannel() != null? categoryMasterDetail.getOutputChannel().getId() : null;
		
		GwtCommandFacade.execute(cmd, new AsyncCallback<StoreItemDetails.Result>() {
			public void onFailure(Throwable caught) {
				savingMessage.cancel();
				StatusMessage.showError(messages.savingCategoryDetailsFailedStatus(), caught);
			}

			public void onSuccess(Result result) {
				savingMessage.cancel();
				StatusMessage.show(messages.savingCategoryDetailsSuccessStatus());
				
				// Invalidate cached category tree.
				GetCategoryTree getTreeCmd = new GetCategoryTree();
				getTreeCmd.catalogId = cmd.catalogId;
				getTreeCmd.outputChannelId = cmd.outputChannelId;
				getTreeCmd.stagingAreaId = cmd.stagingAreaId;
				
				GwtCommandFacade.invalidateCache(getTreeCmd);
				
				// TODO Another option is to return the entire category tree in command and update the cache rather than invalidate it.
				categoryMasterDetail.updateCategory(cmd.itemId, result.storedItemId, null, result.categoryLabels, result.groups, result.parentExtentWithSelf, result.parents, result.propertyData);
			}
		});
	}
}
