package claro.catalog.manager.client.webshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import claro.catalog.command.GetLanguagesByShop;
import claro.catalog.command.shop.FindPromotions;
import claro.catalog.command.shop.FindPromotions.Result;
import claro.catalog.command.shop.GetShops;
import claro.catalog.command.shop.StorePromotion;
import claro.catalog.command.shop.StoreShop;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.catalog.Catalog;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.Shop;
import claro.jpa.shop.VolumeDiscountPromotion;

import com.google.common.base.Objects;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public abstract class ShopModel implements Globals {
	public static int REQUESTED_PAGESIZE;

	private int startIndex;
	private boolean lastPage;
	
	private List<Shop> shops = new ArrayList<Shop>();
	private Shop shop;

	private SMap<Long, SMap<String, String>> referredProducts;
	private List<Promotion> promotions;
	
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
	
	public void setPromotions(List<Promotion> promotions, SMap<Long, SMap<String, String>> referredProducts) {
		this.promotions = promotions;
		this.referredProducts = referredProducts;

		renderAll();
	}
	
	public List<Promotion> getPromotions() {
		return promotions;
	}
	
	public SMap<Long, SMap<String, String>> getReferredProducts() {
		return referredProducts;
	}
	
	public void setShop(Shop shop) {
		this.shop = shop;
		fetchPromotions();
		renderAll();
	}

	public void createWebshop() {
		Shop shop = new Shop();
		Catalog catalog = new Catalog();
		catalog.setId(CatalogManager.getCurrentCatalogId());
		shop.setCatalog(catalog);
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
				super.onSuccess(result);
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
	
	public void store(final StoreShop command) {
		command.catalogId = CatalogManager.getCurrentCatalogId();
		GwtCommandFacade.execute(command, new StatusCallback<StoreShop.Result>(messages.savingWebShopsAction(), false) {
			public void onSuccess(StoreShop.Result result) {
				super.onSuccess(result);
				if (result.shop != null) {
					int index = CollectionUtil.indexOfRef(shops, command.shop);
					if (index != -1) {
						shops.set(index, result.shop);
					}
				} else {
					shops.remove(command.shop);
				}
				setShop(result.shop);
				GwtCommandFacade.invalidateCache(new GetLanguagesByShop(CatalogManager.getCurrentCatalogId()));
			}
		});
	}
	
	
	public void fetchPromotions() {
		FindPromotions cmd = new FindPromotions();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		if (getShop() != null) {
			cmd.shops = Collections.singletonList(getShop());
		}
		
		GwtCommandFacade.execute(cmd, new StatusCallback<FindPromotions.Result>() {
			@Override
			public void onSuccess(Result result) {
				super.onSuccess(result);
				setPromotions(result.promotions, result.referredProducts);
			}
		});
	}
	
	public void store(final StorePromotion command) {
		command.catalogId = CatalogManager.getCurrentCatalogId();
		GwtCommandFacade.execute(command, new StatusCallback<StorePromotion.Result>() { // TODO message.
			public void onSuccess(StorePromotion.Result result) {
				super.onSuccess(result);
				if (promotions == null) {
					setPromotions(new ArrayList<Promotion>(), SMap.<Long, SMap<String, String>>empty());
				}
				if (result.promotion != null) {
					int index = CollectionUtil.indexOfRef(promotions, command.promotion);
					if (index != -1) {
						promotions.set(index, result.promotion);
					}
				} else {
					promotions.remove(command.promotion);
				}
				if (result.additionalReferredProducts != null) {
					for (Entry<Long, SMap<String, String>> referredProduct : result.additionalReferredProducts) {
						referredProducts = referredProducts.set(referredProduct.getKey(), referredProduct.getValue());
					}
				}
				showPromotion(result.promotion != null? result.promotion.getId() : null);
			}
		});
		
	}
	
	public void addNewPromotion(Shop shop, Long productId) {
		VolumeDiscountPromotion newPromotion = new VolumeDiscountPromotion();
		newPromotion.setShop(shop);
		newPromotion.setStartDate(new Date());
		Date endDate = new Date();
		CalendarUtil.addDaysToDate(endDate, 7);
		newPromotion.setEndDate(endDate);
		newPromotion.setVolumeDiscount(10);
		newPromotion.setPrice(0.0);
		newPromotion.setPriceCurrency("EUR");  // TODO i18n

		StorePromotion command = new StorePromotion(newPromotion);
		command.catalogId = CatalogManager.getCurrentCatalogId();
		command.productId = productId;
		
		setShop(shop);
		store(command);
		openDetail();
	}
	
	public void showPromotion(Long promotionId) {
		shopsLoop:
		for (Shop shop : getShops()) {
			for (Promotion promotion : shop.getPromotions()) {
				if (Objects.equal(promotion.getId(), promotionId)) {
					setShop(shop);
					break shopsLoop;
				}
			}
		}
	
		showWebshopPage();
		showPromotions();
		openDetail();
	}
	
	protected abstract void showPromotions();

	protected abstract void showWebshopPage();

	protected abstract void openDetail();
	
	protected abstract void renderAll();

	public String getLanguage() {
		// TODO
		return CatalogManager.getUiLanguage();
	}

}
