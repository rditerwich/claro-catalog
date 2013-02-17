package claro.catalog.manager.client.items;

import claro.jpa.shop.Shop;

public class ItemPageModel {
	private String selectedLanguage;
	private Shop selectedShop;

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public void setSelectedShop(Shop selectedShop) {
		this.selectedShop = selectedShop;
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}
	public Shop getSelectedShop() {
		return selectedShop;
	}
	
	public Long getSelectedShopId() {
		if (selectedShop != null) {
			return selectedShop.getId();
		}
		return null;
	}

}
