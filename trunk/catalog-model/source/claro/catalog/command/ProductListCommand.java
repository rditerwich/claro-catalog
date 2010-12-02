package claro.catalog.command;

import easyenterprise.lib.command.Command;

public class ProductListCommand implements Command<ProductListCommandResult>, CatalogCommand {

	private static final long serialVersionUID = 1L;

	private Long catalogId;
	private Long shopId;
	private String filterString;

	public Long getCatalogId() {
		return catalogId;
	}
	
	public ProductListCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}

	public Long getShopId() {
		return shopId;
	}

	public ProductListCommand setShopId(Long shopId) {
		this.shopId = shopId;
		return this;
	}

	public String getFilterString() {
		return filterString;
	}

	public ProductListCommand setFilterString(String filterString) {
		this.filterString = filterString;
		return this;
	}
}
