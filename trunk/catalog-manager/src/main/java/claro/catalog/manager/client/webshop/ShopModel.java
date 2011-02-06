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
	}


	public void fetchShops() {
	}
	
	protected abstract void showRow(int i);
	protected abstract void renderAll();
}
