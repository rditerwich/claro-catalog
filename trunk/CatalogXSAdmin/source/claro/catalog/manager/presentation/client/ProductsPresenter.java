package claro.catalog.manager.presentation.client;

import java.util.List;

import claro.catalog.manager.presentation.client.catalog.Product;
import claro.catalog.manager.presentation.client.catalog.Category;
import claro.catalog.manager.presentation.client.query.AllProductsQuery;
import claro.catalog.manager.presentation.client.services.CatalogServiceAsync;
import claro.catalog.manager.presentation.client.shop.Shop;

import claro.catalog.manager.presentation.client.ProductView.SHOW;
import claro.catalog.manager.presentation.client.cache.CatalogCache;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProductsPresenter extends CatalogPresenter<ProductsView> {

	private String filter;

	private final ProductPresenter pp = new ProductPresenter(false) {
		private Integer fromIndex = 0;
		private Integer pageSize = 1000;

		@Override
		protected void loadProducts(Shop shop, Category pg) {
			final AllProductsQuery query = new AllProductsQuery();

			query.setStringValue(filter);
			query.setShop(activeShop);
			query.setCategory(currentCategory);
			CatalogServiceAsync.findAllProducts(fromIndex, pageSize, query,
					new AsyncCallback<List<Product>>() {
						@Override
						public void onFailure(Throwable caught) {
							show = SHOW.NO_PRODUCTS;
							show(show);
						}

						@Override
						public void onSuccess(List<Product> result) {
							currentProducts = result;
							for (Product p : result) {
								CatalogCache.get().put(p);
							}
							show = SHOW.PRODUCTS;
							show(show);
						}
					});
		}
	};

	public ProductsPresenter() {
		super(new ProductsView());
		// use a timer to delay starting query
		final Timer t = new Timer() {
			@Override
			public void run() {
				filter = view.getFilterText();
				pp.show(activeShop, currentCategory, true);
			}
		};

		view.init(pp.getView());
		view.productFilterHandlers().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				t.cancel();
				t.schedule(2000); // delay 2 seconds before starting quering
			}
		});
	}

	@Override
	protected void show(Category currentCategory) {
		pp.show(activeShop, currentCategory);
	}

	@Override
	protected void switchLanguage(String newLang) {
		pp.switchLanguage(currentLanguage);
	}
}