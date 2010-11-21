package claro.catalog.manager.presentation.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import claro.catalog.manager.presentation.client.catalog.Catalog;
import claro.catalog.manager.presentation.client.catalog.Category;
import claro.catalog.manager.presentation.client.catalog.Item;
import claro.catalog.manager.presentation.client.catalog.ParentChild;
import claro.catalog.manager.presentation.client.catalog.PropertyValue;
import claro.catalog.manager.presentation.client.services.CatalogServiceAsync;
import claro.catalog.manager.presentation.client.shop.Shop;

import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.cache.CatalogCache.ChangeListener;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.page.Presenter;
import claro.catalog.manager.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public abstract class CatalogPresenter<V extends CatalogView> implements
		Presenter<V> {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	protected final V view;
	protected final HashMap<TreeItem, Long> treemap = new HashMap<TreeItem, Long>();

	protected Shop activeShop;
	protected Category currentCategory;
	protected String currentLanguage = "en";
	protected String currentCatalogName = "Catalog";
	protected Catalog currentCatalog;

	public CatalogPresenter(V cview) {
		this.view = cview;
		view.getPublishButtonClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CatalogServiceAsync.publish(new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(String result) {
						StatusMessage.get().show(i18n.publishSucess());
					}
				});
			}
		});
		view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
			@Override
			public void onOpen(OpenEvent<TreeItem> event) {
				final TreeItem item = event.getTarget();

				if (item.getState()) {
					final Long categoryId = treemap.get(item);

					if (categoryId != null) {
						loadChildren(activeShop, item);
					}
				}
			}
		});
		view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				final TreeItem item = event.getSelectedItem();
				final Long categoryId = treemap.get(item);

				if (categoryId != null) {
					final Entry<Long, String> name = CatalogCache.get().getCategoryNameEntry(categoryId, currentLanguage);

					view.setName(name == null ? "" : name.getValue());
					currentCategory = CatalogCache.get().getCategory(categoryId);
					show(currentCategory);
					loadChildren(activeShop, item);
				} else {
					// FIXME ?? can this happen?
				}
			}

		});
		view.getLanguageChangeHandler().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				currentLanguage = view.getSelectedLanguage();
				switchLanguage(currentLanguage);
			}
		});
		
		CatalogCache.get().listenToCurrentCatalog(new ChangeListener<Catalog>() {
			public void changed(Catalog newValue) {
				if (newValue != null) {
					currentCatalogName = newValue.getName();
				}
			}
		});
		
		CatalogCache.get().listenToCurrentShop(new ChangeListener<Shop>() {
			public void changed(Shop newValue) {
				if (newValue != null) {
					CatalogCache.get().getShop(newValue.getId(), new AsyncCallback<Shop>() {
						public void onFailure(Throwable caught) {
							// TODO 
						}
						public void onSuccess(Shop result) {
							activeShop = result;
							CatalogCache.get().getCatalog(currentCatalogName, new AsyncCallback<Catalog>() {
								public void onFailure(Throwable caught) {
									// TODO ??
								}
								
								public void onSuccess(Catalog result) {
									// TODO Shop vs Outputchannel
									activeShop = (Shop) result.getOutputChannels().get(0);
									view.setLanguages(result.getLanguages(), "en");
									CatalogCache.get().loadCategoryNames(
											new AsyncCallback<String>() {
												@Override
												public void onFailure(Throwable caught) {
												}
												
												@Override
												public void onSuccess(String result) {
													loadChildren(activeShop, null); // initial tree
													for (Category pg : Util.categories(activeShop.getNavigation())) {
														CatalogCache.get().put(pg);
													}
												}
											});
								}
							});
						}
					});
				}
			}
		});
		
	}

	@Override
	public V getView() {
		return view;
	}

	public void show() {
		view.setLanguages(CatalogCache.get().getCurrentCatalog().getLanguages(), "en");
	}

	protected abstract void show(Category currentCategory);

	protected abstract void switchLanguage(String newLang);

	protected void loadChildren(Shop shop, final TreeItem parent) {
		if (!view.getTree().isTreeItemEmpty(parent)) {
			return;
		}
		reloadChildren(shop, parent);
	}

	protected void reloadChildren(Shop shop, final TreeItem parent) {
		final Category parentPG = CatalogCache.get().getCategory(treemap.get(parent));

		CatalogServiceAsync.findAllCategoryChildren(shop, parentPG,
				new AsyncCallback<List<Category>>() {
					@Override
					public void onFailure(Throwable caught) {
						// StatusMessage.get().show(caught.getMessage(), 30);
					}

					@Override
					public void onSuccess(List<Category> result) {
						if (result.isEmpty()) {
							view.getTree().setTreeItemAsEmpty(parent);
						}
						
						Map<TreeItem, Long> unusedChildren = new HashMap<TreeItem, Long>();
						for (int i = 0; i < view.getTree().getItemCount(parent); i++) {
							TreeItem child = view.getTree().getItem(parent, i);
							unusedChildren.put(child, treemap.get(child));
						}
						
						for (Category category : result) {
							final PropertyValue value = Util.getPropertyValueByName(category.getPropertyValues(), Util.NAME, currentLanguage);

							if (CatalogCache.get().getNameCategory() == null && value != null && category.getId().equals(value.getProperty().getItem().getId())) {
								CatalogCache.get().setNameCategory(category);
								CatalogCache.get().setCategoryProduct(category);
							}
							CatalogCache.get().put(category);
							for (ParentChild pPG : category.getParents()) {
								CatalogCache.get().put(pPG.getParent());
							}
							TreeItem found = null;
							for (int i = 0; i < view.getTree().getItemCount(parent); i++) {
								final TreeItem item = view.getTree().getItem(parent, i);

								Long existingCategoryId = treemap.get(item);
								if (existingCategoryId != null && category.getId().equals(existingCategoryId)) {
									found = item;
								}
							}
							if (found != null) {
								found.setText(Util.name(category, currentLanguage));
								unusedChildren.remove(found);
							} else {
								String newCategoryName = value != null ? value.getStringValue() : "<No Name>";
								TreeItem newChild = view.getTree().addItem(parent, newCategoryName);
								treemap.put(newChild, category.getId());
								
								// Check whether this current is a new category (id null) and it has the same name:
								if (currentCategory != null) {
									String currentCategoryName = Util.name(currentCategory, currentLanguage);
									if (currentCategory.getId() == null && currentCategoryName != null && currentCategoryName.equals(newCategoryName)) {
										currentCategory = category;
										view.getTree().setSelectedItem(newChild);
									}
								}
							}
						}
						
						// Clean up unused children:
						for (TreeItem unused : unusedChildren.keySet()) {
							unused.remove();
						}
						
						// Make sure something is selected:
						if (view.getTree().getSelectedItem() == null) {
							if (view.getTree().getItemCount() > 0) {
								view.getTree().setSelectedItem(view.getTree().getItem(0));
							}
						}
					}
				});
	}
}
