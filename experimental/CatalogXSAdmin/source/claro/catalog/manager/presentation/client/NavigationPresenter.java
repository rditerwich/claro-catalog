package claro.catalog.manager.presentation.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import claro.catalog.manager.presentation.client.catalog.Category;
import claro.catalog.manager.presentation.client.catalog.Language;
import claro.catalog.manager.presentation.client.shop.Navigation;
import claro.catalog.manager.presentation.client.shop.Shop;

import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.cache.CatalogCache.ChangeListener;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.page.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public class NavigationPresenter implements Presenter<NavigationView> {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	private final HashMap<TreeItem, Navigation> treemap = new HashMap<TreeItem, Navigation>();

	private final NavigationView view;
	private final ItemParentsPresenter pp;
	private String currentLanguage = "en";

	public NavigationPresenter() {
		view = new NavigationView();
		view.setCurrentLanguage(currentLanguage);
		pp = new ItemParentsPresenter(view.getItemParentsView());
		view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				Navigation selection = treemap.get(event.getSelectedItem());
				view.setCurrentLanguage(currentLanguage);
				view.getCurrentNavigationBinding().setData(selection);
			}
		});
//		pp.setDeleteHandler(new DeleteHandler<Long>() {
//			@Override
//			public void onDelete(Long pid) {
//				final TreeItem ti = findTreeItem(pid);
//
//				if (ti != null) {
//					treemap.remove(ti);
//					view.getTree().removeItem(ti);
//				}
//				topLevelCategorys.remove(CatalogCache.get().getCategoryNameEntry(
//						pid, currentLanguage));
//			}
//		});
//		view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				final Shop oldShop = activeShop.clone(new HashMap());
//				final List<Category> p = new ArrayList<Category>();
//
//				for (Entry<Long, String> pge : topLevelCategorys) {
//					Category pg = CatalogCache.get().getCategory(pge.getKey());
//					if (pg == null) {
//						pg = new Category();
//						pg.setId(pge.getKey());
//					}
//					p.add(pg);
//				}
//				// TODO Change this:
//				// activeShop.setTopLevelCategorys(p);
//
//				ShopServiceAsync.updateShop(oldShop, activeShop,
//					new AsyncCallback<Shop>() {
//						@Override
//						public void onFailure(Throwable caught) {
//							// TODO Auto-generated method stub
//						}
//
//						@Override
//						public void onSuccess(Shop result) {
//							CatalogCache.get().put(result);
//							StatusMessage.get()
//									.show(i18n.navigationSaved());
//						}
//					});
//			}
//		});

		CatalogCache.get().listenToCurrentShop(new ChangeListener<Shop>() {
			public void changed(Shop currentShop) {
				reloadNavigationTree();
			}
		});
		
		CatalogCache.get().listenToCurrentLanguage(new ChangeListener<Language>() {
			public void changed(Language currentLanguage) {
				if (currentLanguage != null) {
					NavigationPresenter.this.currentLanguage = currentLanguage.getName();
				} else {
					NavigationPresenter.this.currentLanguage = null;
				}
				reloadNavigationTree();
			}
		});
		
		CatalogCache.get().loadCategoryNames(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
			}
			public void onSuccess(String result) {
				reloadNavigationTree();
			}
		});
	}
	
	private void reloadNavigationTree() {
		Shop currentShop = CatalogCache.get().getCurrentShop();
		if (currentShop != null) {
			CatalogCache.get().getShop(currentShop.getId(), new AsyncCallback<Shop>() {
				public void onFailure(Throwable caught) {
					// TODO Reload?? message??
				}
				
				public void onSuccess(Shop result) {
					TreeItem root;
					if (view.getTree().getItemCount() > 0) {
						root = view.getTree().getItem(0);
						root.setText(result.getName());
					} else {
						root = view.getTree().addItem(result.getName());
					}
					
					loadChildren(result.getNavigation(), root);
				}
			});
		} else {
			// TODO print some no shop message.
		}
	}

	protected void loadChildren(List<Navigation> navigations, TreeItem parent) {
		Map<TreeItem, Navigation> unusedChildren = new HashMap<TreeItem, Navigation>();
		for (int i = 0; i < view.getTree().getItemCount(parent); i++) {
			TreeItem child = view.getTree().getItem(parent, i);
			unusedChildren.put(child, treemap.get(child));
		}
		
		for (Navigation navigation : navigations) {
			String navigationCategoryName;
			if (navigation.getCategory() != null) {
				navigationCategoryName = CatalogCache.get().getCategoryName(navigation.getCategory().getId(), currentLanguage);
			} else {
				navigationCategoryName = "group";
			}
			
			// Find existing item
			TreeItem child = null;
			for (int i = 0; i < view.getTree().getItemCount(parent); i++) {
				final TreeItem item = view.getTree().getItem(parent, i);

				Navigation existingNavigation = treemap.get(item);
				if (existingNavigation != null && navigation.getId().equals(existingNavigation)) {
					child = item;
				}
			}
			
			if (child != null) {
				child.setText(navigationCategoryName);
				unusedChildren.remove(child);
			} else {
				
				child = view.getTree().addItem(parent, navigationCategoryName);
				treemap.put(child, navigation);
				
//				// Check whether this current is a new category (id null) and it has the same name:
//				if (currentCategory != null) {
//					String currentCategoryName = Util.name(currentCategory, currentLanguage);
//					if (currentCategory.getId() == null && currentCategoryName != null && currentCategoryName.equals(navigationCategoryName)) {
//						currentCategory = category;
//						view.getTree().setSelectedItem(newChild);
//					}
//				}
			}
			
			// Recurse down
			loadChildren(navigation.getSubNavigation(), child);
		}
		
		// Clean up unused children:
		for (TreeItem unused : unusedChildren.keySet()) {
			unused.remove();
		}
	}

	@Override
	public NavigationView getView() {
		return view;
	}

	public void show() {
		show(currentLanguage);
	}

	public void show(String lang) {
	}
}
