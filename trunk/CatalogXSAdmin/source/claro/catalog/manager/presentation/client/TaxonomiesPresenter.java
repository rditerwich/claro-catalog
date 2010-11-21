package claro.catalog.manager.presentation.client;

import claro.catalog.manager.presentation.client.catalog.Category;

import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;

public class TaxonomiesPresenter extends CatalogPresenter<TaxonomiesView> {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	private static final int TAB_PRODUCT = 1;
	private static final int TAB_CATEGORY = 0;

	private final CategoryPresenter categoriesPresenter = new CategoryPresenter() {
		protected void onSaved(Category savedCategory) {
			reloadChildren(activeShop, view.getTree().getSelectedItem().getParentItem());
		};
	};
	final ProductPresenter productPresenter = new ProductPresenter(true);

	public TaxonomiesPresenter() {
		super(new TaxonomiesView());
		view.detailPanel.add(categoriesPresenter.getView());
//		view.addTab(categoriesPresenter.getView(), i18n.category());
//		view.addTab(productPresenter.getView(), i18n.products());

		view.getNewCategoryButtonClickHandler().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						TreeItem selection = view.getTree().getSelectedItem();
						final Long lastCategory = treemap.get(selection);

						view.setName("&lt;" + i18n.newCategory() + "&gt;");
						currentCategory = categoriesPresenter.setNewCategory(activeShop, lastCategory != null ? CatalogCache.get().getCategory(lastCategory) : null);
//						productPresenter.show(activeShop, currentCategory);

						// Temporarily show item in the tree.
						TreeItem newChild = selection.addItem(i18n.newCategory());
						selection.setState(true);
						view.getTree().setSelectedItem(newChild, false); // False: Do not notify anyone of selection
						
						// view.getTree().deSelectItem();
//						view.selectedTab(TAB_CATEGORY);
					}
				});
//		view.addTabSelectionHandler(new SelectionHandler<Integer>() {
//			@Override
//			public void onSelection(SelectionEvent<Integer> event) {
//				if (event.getSelectedItem().intValue() == TAB_CATEGORY) {
//					categoriesPresenter.show(currentCategory);
//				} else {
//					productPresenter.show(activeShop, currentCategory);
//				}
//			}
//		});
	}

	protected void show(Category currentCategory) {
//		if (Boolean.FALSE.equals(currentCategory.getContainsProducts())) {
//			view.setTabVisible(TAB_PRODUCT, false);
//			view.selectedTab(TAB_CATEGORY);
//		} else {
//			view.setTabVisible(TAB_PRODUCT, true);
//		}
//		if (view.getSelectedTab() == TAB_CATEGORY) {
			categoriesPresenter.show(currentCategory);
//		} else {
//			productPresenter.show(activeShop, currentCategory);
//		}
	}

	protected void switchLanguage(String newLang) {
		categoriesPresenter.switchLanguage(currentLanguage);
//		productPresenter.switchLanguage(currentLanguage);
	}
}
