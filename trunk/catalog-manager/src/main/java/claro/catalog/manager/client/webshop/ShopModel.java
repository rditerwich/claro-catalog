package claro.catalog.manager.client.webshop;

import java.util.ArrayList;
import java.util.List;

import claro.jpa.shop.Shop;

public abstract class ShopModel {

	private List<Shop> shops = new ArrayList<Shop>();
	private Shop shop;
	
	public List<Shop> getShops() {
		return shops;
	}

	public Shop getShop() {
		return shop;
	}
	
	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public void createWebshop() {
		Shop shop = new Shop();
		shop.setName("new shop");
		getShops().add(0, shop);
		renderAll();
		setShop(shop);
	}


	public void fetchShops() {
	}
	
	protected abstract void showRow(int i);
	protected abstract void renderAll();
}
