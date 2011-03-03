package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import claro.catalog.command.items.GetCategoryTree;
import claro.catalog.data.ItemType;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.catalog.Category;

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
// TODO Change from categories to generic item selection.
public class ItemSelectionWidget extends SelectionWidget<Long> {
	private enum ExtraStyles implements Style { categoryTree }
	
	private PopupPanel addCategoryPanel;
	

	private SMap<Long, SMap<String, String>> categories = SMap.empty();
	private String language;

	private Collection<Category> pendingSetDataCategories;
	private ItemType itemType;

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
	
	public void setData(Collection<Category> categories, String language) {
		pendingSetDataCategories = categories;
		SMap<Long, SMap<String, String>> data = SMap.empty();
		for (Category category : categories) {
			SMap<String, String> labels = SMap.create((String) null, "" + category.getId());
			data = data.add(category.getId(), labels);
		}
		setData(data, language);
		fetchCategories();
	}
	
	public void setData(SMap<Long, SMap<String, String>> categories, String language) {
		
		this.categories = categories;
		this.language = language;
		setSelection(categories.getKeys());
	}

	public String displayName(Long categoryId) {
		return categories.getOrEmpty(categoryId).tryGet(language, null);
	}
	
	
	protected void getDisplayNames(List<Long> selection, DisplayNamesCallback displayNamesCallback) {
		List<String> result = new ArrayList<String>();
		for (Long selectedObject : selection) {
			result.add(displayName(selectedObject));
		}
		
		// TODO if not all are available, retrieve.
		
		displayNamesCallback.displayNames(result);
	}


	protected void addSelectionClicked(Widget addWidget) {
		if (addCategoryPanel == null) {
			addCategoryPanel = new PopupPanel(true) {{
				StyleUtil.addStyle(this, ExtraStyles.categoryTree);
				setWidget(new ScrollPanel(new Label(messages.loading())) {{
					setPixelSize(200, 400);
				}});
			}};
		}

		// Update tree
		fetchCategories();
		
		addCategoryPanel.showRelativeTo(addWidget);
	}

	protected void addCategory(Long categoryId, SMap<String, String> labels) {
		categories = categories.add(categoryId, labels);
		addSelectedObject(categoryId);
	}
	
	@Override
	protected void removeObjectClicked(Long categoryId) {
		removeCategory(categoryId);
	}

	protected void removeCategory(Long categoryId) {
		SMap<Long, SMap<String, String>> newCats = SMap.empty();
		for (Long existingCat : categories.getKeys()) {
			if (!existingCat.equals(categoryId)) {
				newCats = newCats.add(existingCat, categories.get(existingCat));
			}
		}
		categories = newCats;
		removeSelectedObject(categoryId);
	}

	protected String getAddToSelectionLabel() {
		return messages.addToCategoriesLink();
	}

	// TODO create filtered selection tree?
	private Tree createTree(final Long rootId, final SMap<Long, SMap<String, String>> categories, final SMap<Long, Long> children) {
		Tree result = new Tree();
		// Get root categories:
		List<Long> rootCategories = children.getAll(rootId);
		if (rootCategories.isEmpty()) {
			return null;
		}

		for (final Long root : rootCategories) {
			String categoryName = categories.getOrEmpty(root).tryGet(CatalogManager.getUiLanguage(), null);
			result.addItem(new TreeItem(categoryName) {{
				StyleUtil.addStyle(this, Styles.selectionDisplayName);
				setUserObject(root);
				addItem(""); // uninitialized marker.
			}});
		}

		result.addOpenHandler(new OpenHandler<TreeItem>() {
			public void onOpen(OpenEvent<TreeItem> event) {
				// Are the children uninitialized?
				TreeItem target = event.getTarget();
				if (target.getChildCount() == 1 && target.getChild(0).getUserObject() == null) {
					target.removeItem(target.getChild(0));
					// Get child categories:
					List<Long> childCategories = children.getAll((Long) target.getUserObject());
					for (final Long child : childCategories) {
						String categoryName = categories.getOrEmpty(child).tryGet(CatalogManager.getUiLanguage(), null);
						target.addItem(new TreeItem(categoryName) {{
							StyleUtil.addStyle(this, Styles.selectionDisplayName);
							setUserObject(child);
							addItem(""); // uninitialized marker.
						}});
					}
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
				Object categoryId = event.getSelectedItem().getUserObject();
				if (categoryId != null) {
					addCategory((Long)categoryId, categories.get((Long)categoryId));
					addCategoryPanel.hide();
				}
			}
		});
		
		result.getItem(0).setState(true);
		
		return result;
	}
	
	private void fetchCategories() {
		GetCategoryTree c = new GetCategoryTree();
		c.catalogId = CatalogManager.getCurrentCatalogId();
		c.outputChannelId = null; // TODO ???
		GwtCommandFacade.executeCached(c, 1000 * 60 * 60, new StatusCallback<GetCategoryTree.Result>() {
			public void onSuccess(GetCategoryTree.Result result) {
				
				// pending set-data
				if (pendingSetDataCategories != null) {
					SMap<Long, SMap<String, String>> data = SMap.empty();
					for (Category category : pendingSetDataCategories) {
						SMap<String, String> labels = result.categories.get(category.getId(), SMap.create((String) null, "" + category.getId()));
						data = data.add(category.getId(), labels);
					}
					pendingSetDataCategories = null;
					setData(data, language);
				}
				
				// TODO Only create tree if data changed??
				if (addCategoryPanel != null) {
					Widget panelWidget = createTree(result.root, result.categories, result.children);
					if (panelWidget == null) {
						panelWidget = new Label(messages.noCategoriesAvailable());
					}
					((ScrollPanel)addCategoryPanel.getWidget()).setWidget(panelWidget);
				}
			}
		});
	}
}
