package claro.catalog.command;

import easyenterprise.lib.command.Command;

public class ProductListCommand implements Command<ProductListCommandResult> {

	private static final long serialVersionUID = 1L;

	private Long catalogId;
	private Long outputChannelId;
	private String language;
	private String filterString;

	public Long getCatalogId() {
		return catalogId;
	}
	
	public ProductListCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}

	public Long getOutputChannelId() {
		return outputChannelId;
	}

	public ProductListCommand setOutputChannelId(Long outputChannelId) {
		this.outputChannelId = outputChannelId;
		return this;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public ProductListCommand setLanguage(String language) {
		this.language = language;
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
