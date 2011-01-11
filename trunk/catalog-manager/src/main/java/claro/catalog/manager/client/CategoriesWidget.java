package claro.catalog.manager.client;

import java.util.List;

import claro.catalog.command.items.GetCategoryTree;
import claro.catalog.manager.client.command.StatusCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
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

public class CategoriesWidget extends Composite implements Globals {
	
	enum Styles implements Style { mouseOverStyle, categoryStyle, categoryName, categoryAdd, categoryTree }
	
	private FlowPanel mainPanel;
	private PopupPanel addCategoryPanel;
	

	private final boolean canSelect;
	

	public CategoriesWidget() {
		this(true);
	}
	
	public CategoriesWidget(boolean canSelect) {
		this.canSelect = canSelect;
		initWidget(mainPanel = new FlowPanel() {{
			StyleUtil.add(this, Styles.categoryStyle);
		}});
	}
	
	public void setData(SMap<Long, SMap<String, String>> categories, String language) {
		
		final List<Long> categoryKeys = categories.getKeys();
		mainPanel.clear();
		int i = 0;
		for (Long categoryId : categoryKeys) {
			final boolean lastCategory = i++ == categoryKeys.size() - 1;
			final Long catId = categoryId;
			final String categoryName = categories.getOrEmpty(categoryId).tryGet(language, null);
			mainPanel.add(new Grid(1, lastCategory ? 3 : 2) {{
				setWidget(0, 0, new Anchor(categoryName) {{
					StyleUtil.add(this, Styles.categoryName);
					setTitle(getCategoryTooltip(categoryName));
					if (canSelect) {
						addHoverStyles(this);
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								categoryClicked(catId);
							}
						});
					}
				}});
				setWidget(0, 1, new Anchor("X") {{
					addHoverStyles(this);
					setTitle(getRemoveCategoryTooltip(categoryName));
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							removeCategory(catId);
						}
					});
				}});
				if (lastCategory) {
					setWidget(0, 2, createAddAnchor("+"));
				}
			}});
		}
		if (categoryKeys.isEmpty()) {
			mainPanel.add(createAddAnchor(messages.addCategoriesLink()));
		}
	}

	private Anchor createAddAnchor(String addCategoryText) {
		return new Anchor(addCategoryText) {{ // TODO Use image instead?
			StyleUtil.add(this, Styles.categoryAdd);
			addHoverStyles(this);
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (addCategoryPanel == null) {
						addCategoryPanel = new PopupPanel(true) {{
							StyleUtil.add(this, Styles.categoryTree);
							setWidget(new ScrollPanel(new Label(messages.loading())));
						}};
					}

					// Update tree
					GetCategoryTree c = new GetCategoryTree();
					c.catalogId = CatalogManager.getCurrentCatalogId();
					c.outputChannelId = null; // TODO ???
					GwtCommandFacade.executeCached(c, 1000 * 60 * 60, new StatusCallback<GetCategoryTree.Result>() {
						public void onSuccess(GetCategoryTree.Result result) {
							// TODO Only create tree if data changed??
							((ScrollPanel)addCategoryPanel.getWidget()).setWidget(createTree(result.root, result.categories, result.children));
						}
					});
					
					addCategoryPanel.showRelativeTo((Widget) event.getSource());
				}
			});
			
			// TODO add show on mouse over.
		}};
	}
	
	protected String getCategoryTooltip(String categoryName) {
		return "";
	}
	
	protected String getRemoveCategoryTooltip(String categoryName) {
		return "";
	}
	
	protected String getAddCategoryTooltip() {
		return "";
	}
	
	protected void categoryClicked(Long categoryId) {
		
	}
	
	protected void addCategory(Long categoryId, SMap<String, String> labels) {
		
	}

	protected void removeCategory(Long categoryId) {
		
	}
	
	// TODO create filtered selection tree?
	private Tree createTree(final Long root, final SMap<Long, SMap<String, String>> categories, final SMap<Long, Long> children) {
		Tree result = new Tree();
		
		result.addItem(new TreeItem(messages.categoryTreeRootName()) {{
			setUserObject(root);
			addItem(""); // Add uninitialized marker.
		}});

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
							StyleUtil.add(this, Styles.categoryName);
							setUserObject(child);
							addItem(""); // uninitialized marker.
						}});
					}
				}
			}
		});
		
		result.addSelectionHandler(new SelectionHandler<TreeItem>() {
			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				Object categoryId = event.getSelectedItem().getUserObject();
				if (categoryId != null) {
					addCategory((Long)categoryId, categories.get((Long)categoryId));
				}
			}
		});
		
		return result;
	}
	
	private void addHoverStyles(final Anchor anchor) {
		anchor.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				StyleUtil.add(anchor, Styles.mouseOverStyle);
			}
		});
		anchor.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				StyleUtil.remove(anchor, Styles.mouseOverStyle);
			}
		});
	}
}
