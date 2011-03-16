package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import claro.catalog.command.items.DisplayNames;
import claro.catalog.command.items.GetItemTree;
import claro.catalog.command.items.DisplayNames.Result;
import claro.catalog.data.ItemData;
import claro.catalog.data.ItemType;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.command.StatusCallback;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.SMap;

// TODO Allow changing of order of categories.
public class ItemSelectionWidget extends SelectionWidget<Long> {
	private enum ExtraStyles implements Style { categoryTree }
	
	private PopupPanel addItemPanel;

	// selected items
	private SMap<Long, SMap<String, String>> displayNames = SMap.empty();
	private String language;

	private ItemType itemType;
	
	// Selection popup data:
	private static final Object loadingChildren = new Object();
	private Long rootId;
	private SMap<Long, ItemData> treeItems;
	private Map<Long, List<Long>> children = new LinkedHashMap<Long, List<Long>>();
	private Map<Long, TreeItem> loadingTreeItems = new HashMap<Long, TreeItem>();

	private Long outputChannelId;

	
	

	public ItemSelectionWidget() {
		this(true, ItemType.category, true);
	}
	
	public ItemSelectionWidget(boolean canSelectSelection, ItemType itemType, boolean multiSelect) {
		super(canSelectSelection, multiSelect);
		this.itemType = itemType;
	}

	/**
	 * Note: does *not* re-render. Only {@link #setData(Collection, String)} re-renders.
	 * @param itemType
	 */
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	
	public void setData(Collection<Long> itemIds, String language) {
		setSelection(itemIds);
		this.language = language;
	}
	
	public void setData(SMap<Long, SMap<String, String>> items, String language) {
		
		this.displayNames = items;
		this.language = language;
		setSelection(items.getKeys());
	}

	public String displayName(Long itemId) {
		return displayNames.getOrEmpty(itemId).tryGet(language, null);
	}
	
	
	protected void getDisplayNames(List<Long> selection, final DisplayNamesCallback displayNamesCallback) {
		List<Long> missingLabels = displaySelection(selection, displayNamesCallback);

		if (!missingLabels.isEmpty()) {
			DisplayNames cmd = new DisplayNames();
			cmd.catalogId = CatalogManager.getCurrentCatalogId();
			cmd.outputChannelId = outputChannelId;
			cmd.itemIds = missingLabels;
			GwtCommandFacade.execute(cmd, new StatusCallback<DisplayNames.Result>() {
				public void onSuccess(Result result) {
					super.onSuccess(result);
					displayNames = displayNames.setAll(result.items);
					
					displaySelection(getSelection(), displayNamesCallback);
				}
			});
		}
	}

	private List<Long> displaySelection(List<Long> selection, DisplayNamesCallback displayNamesCallback) {
		List<String> names = new ArrayList<String>();
		List<Long> missingLabels = new ArrayList<Long>();
		for (Long selectedObject : selection) {
			String displayName = displayName(selectedObject);
			if (displayName != null) {
				names.add(displayName);
			} else {
				names.add("" + selectedObject);
				missingLabels.add(selectedObject);
			}
		}
		
		displayNamesCallback.displayNames(names);
		return missingLabels;
	}


	protected void addSelectionClicked(Widget addWidget) {
		showTree(addWidget);
	}

	protected void addItem(Long itemId, SMap<String, String> labels) {
		displayNames = displayNames.set(itemId, labels);
		addSelectedObject(itemId);
	}
	
	@Override
	protected void removeObjectClicked(Long itemId) {
		removeItem(itemId);
	}

	protected void removeItem(Long itemId) {
		displayNames = displayNames.removeKey(itemId);
		removeSelectedObject(itemId);
	}

	protected String getAddToSelectionLabel() {
		switch (itemType) {
		case category:
			return messages.addToCategoriesLink();
		case product:
			return messages.addToProductsLink();
		default:
			return messages.addToItemsLink();
		}
	}

	private void showTree(Widget addWidget) {
		if (addItemPanel == null) {
			addItemPanel = new PopupPanel(true) {{
				StyleUtil.addStyle(this, ExtraStyles.categoryTree);
				setWidget(new ScrollPanel(new Label(messages.loading())) {{
					setPixelSize(200, 400);
				}});
				addCloseHandler(new CloseHandler<PopupPanel>() {
					public void onClose(CloseEvent<PopupPanel> event) {
						rootId = null;
						treeItems = null;
						children = null;
					}
				});
			}};
		}

		// Show a message while were are fetching the tree.
		((ScrollPanel)addItemPanel.getWidget()).setWidget(new Label(messages.loading()));

		children = new HashMap<Long, List<Long>>();
		treeItems = SMap.empty();
		
		// Update tree
		fetchSubTree(null);
		
		addItemPanel.showRelativeTo(addWidget);
	}

	// TODO create filtered selection tree?
	private Tree createTree(final Long rootId, SMap<Long, ItemData> treeItemLabels, Map<Long, List<Long>> children) {
		this.treeItems = this.treeItems.setAll(treeItemLabels);
		this.loadingTreeItems.clear();

		Tree result = new Tree();
		// Get root categories:
		List<Long> rootItems = children.get(rootId);
		if (rootItems == null ||  rootItems.isEmpty()) {
			return null;
		}

		for (final Long root : rootItems) {
			String itemName = treeItemLabels.get(root, ItemData.empty).displayNames.tryGet(CatalogManager.getUiLanguage(), null);
			result.addItem(new TreeItem(itemName) {{
				StyleUtil.addStyle(this, Styles.selectionDisplayName);
				setUserObject(root);
				addItem(""); // uninitialized marker.
			}});
		}

		result.addOpenHandler(new OpenHandler<TreeItem>() {
			public void onOpen(OpenEvent<TreeItem> event) {
				// Are the children uninitialized?
				TreeItem target = event.getTarget();
				
				boolean fetchRequired = false;
				if (childrenUninitialized(target)) {
					List<Long> childCategories = ItemSelectionWidget.this.children.get((Long) target.getUserObject());
					if (childCategories != null) {
						target.removeItems();
						for (final Long child : childCategories) {
							String categoryName = ItemSelectionWidget.this.treeItems.get(child, ItemData.empty).displayNames.tryGet(CatalogManager.getUiLanguage(), null);
							target.addItem(new TreeItem(categoryName) {{
								StyleUtil.addStyle(this, Styles.selectionDisplayName);
								setUserObject(child);
								addItem(""); // uninitialized marker.
							}});
							
							// See whether direct children are retrieved.
							if (!ItemSelectionWidget.this.children.containsKey(child)) {
								fetchRequired = true;
							}
						}
					} else {
						setLoadingChildren(target);
						fetchRequired = true;
					}
					
				} else if (!isLoadingChildren(target)) {
					List<Long> childCategories = ItemSelectionWidget.this.children.get((Long) target.getUserObject());
					for (final Long child : childCategories) {
						// See whether direct children are retrieved.
						if (!ItemSelectionWidget.this.children.containsKey(child)) {
							fetchRequired = true;
							break;
						}
					}
				}
				
				if (fetchRequired) {
					fetchSubTree((Long) target.getUserObject());
				}
			}

		});
		
		// Make sure the root cannot be closed:
		result.addCloseHandler(new CloseHandler<TreeItem>() {
			public void onClose(CloseEvent<TreeItem> event) {
				if (event.getTarget() == event.getTarget().getTree().getItem(0)) {
					event.getTarget().setState(true, false);
				}
			}
		});
		
		result.addSelectionHandler(new SelectionHandler<TreeItem>() {
			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				Object itemId = event.getSelectedItem().getUserObject();
				if (itemId != null) {
					ItemData itemData = ItemSelectionWidget.this.treeItems.get((Long)itemId);
					if (itemData != null && (itemType == ItemType.item || itemData.type == itemType)) {
						addItem((Long)itemId, itemData.displayNames);
						addItemPanel.hide();
					}
				}
			}
		});
		
//		result.getItem(0).setState(true);
		
		return result;
	}
	
	private void updateTree(SMap<Long, ItemData> items, Map<Long, List<Long>> children) {
		this.treeItems = this.treeItems.setAll(items);
		this.children.putAll(children);
		
		for (Entry<Long, List<Long>> parentChild : children.entrySet()) {
			if (parentChild.getValue() != null) {
				TreeItem loadingItem = loadingTreeItems.get(parentChild.getKey());
				if (isLoadingChildren(loadingItem)) {
					// clear loading:
					loadingItem.getChild(0).setUserObject(null);
					
					// reopen item:
					if (loadingItem.getState()) {
						loadingItem.setState(false, false);
						loadingItem.setState(true);
					}
				}
			}
		}
	}

	private boolean childrenUninitialized(TreeItem item) {
		return item != null && item.getChildCount() == 1 && item.getChild(0).getUserObject() == null;
	}

	private boolean isLoadingChildren(TreeItem item) {
		return item != null && item.getChildCount() == 1 && item.getChild(0).getUserObject() == loadingChildren;
	}
	
	private void setLoadingChildren(TreeItem item) {
		item.getChild(0).setText(messages.loading()); // TODO possibly other message? 
		item.getChild(0).setUserObject(loadingChildren);
		loadingTreeItems.put((Long) item.getUserObject(), item);
	}

	
	private void fetchSubTree(Long subTreeRoot) {
		GetItemTree c = new GetItemTree();
		c.catalogId = CatalogManager.getCurrentCatalogId();
		c.outputChannelId = null; // TODO ???
		c.rootId = subTreeRoot;
		c.depth = 2;
		c.categoriesOnly = itemType == ItemType.category;
		
		System.out.println("Fetching subtree: " + subTreeRoot);
		// TODO Make a more generic invalidator for cache!!
		GwtCommandFacade.executeCached(c, 1000 * 60 * 15, new StatusCallback<GetItemTree.Result>() {
			public void onSuccess(GetItemTree.Result result) {
				System.out.println("Fetched subtree: " + result.root);
				if (rootId == null) {
					rootId = result.root;
					Widget panelWidget = createTree(rootId, result.items, result.children);
					if (panelWidget == null) {
						panelWidget = new Label(messages.noCategoriesAvailable());
					}
					((ScrollPanel)addItemPanel.getWidget()).setWidget(panelWidget);
				} else {
					updateTree(result.items, result.children);
				}
			}
		});
	}
}
