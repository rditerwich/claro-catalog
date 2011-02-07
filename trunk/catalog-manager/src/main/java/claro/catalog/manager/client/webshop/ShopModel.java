package claro.catalog.manager.client.webshop;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import claro.catalog.command.importing.GetImportSources;
import claro.catalog.command.shop.GetShops;
import claro.catalog.command.shop.StoreShop;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.shop.Shop;
import easyenterprise.lib.command.gwt.GwtCommandFacade;

public abstract class ShopModel implements Globals {
	public static int REQUESTED_PAGESIZE;

	private int startIndex;
	private boolean lastPage;
	
	private List<Shop> shops = new ArrayList<Shop>();
	private Shop shop;
	
	public List<Shop> getShops() {
		return shops;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
		renderAll();
	}
	
	public void setShop(Shop shop) {
		this.shop = shop;
		renderAll();
	}

	public void createWebshop() {
		Shop shop = new Shop();
		shop.setName("new shop");
		getShops().add(0, shop);
		setShop(shop);
		openDetail();
	}


	/**
	 * Fetches the current page
	 */
	public void fetchShops() {
		fetchShops(false);
	}
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public void fetchPreviousPage() {
		
	}
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public void fetchNextPage() {
		
	}

	public void fetchShops(boolean wait) {
		// TODO For now, ignore wait.  Use PagedView in future.
		GetShops command = new GetShops();
		command.catalogId = CatalogManager.getCurrentCatalogId();
		GwtCommandFacade.execute(command, new StatusCallback<GetShops.Result>(messages.loadingWebShopsMessage()) {
			public void onSuccess(GetShops.Result result) {
				setShops(result.shops);
			}
		});
	}
	
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public boolean isLastPage() {
		return lastPage;
	}
	
	public void store(StoreShop command) {
		// TODO Auto-generated method stub
		// TODO Invalidate languagesByShops command.
	}

	protected abstract void openDetail();
	
	protected abstract void renderAll();

}
