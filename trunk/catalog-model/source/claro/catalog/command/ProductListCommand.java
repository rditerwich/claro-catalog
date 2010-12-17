package claro.catalog.command;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.util.Paging;

public class ProductListCommand implements Command<ProductListCommandResult> {

	private static final long serialVersionUID = 1L;

	public Long catalogId;
	public Long stagingAreaId;
	public Long outputChannelId;
	public String uiLanguage;
	public String language;
	public String filterString;
	public Paging paging = Paging.NO_PAGING;

	public ProductListCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}
	public ProductListCommand setStagingAreaId(Long stagingAreaId) {
		this.stagingAreaId = stagingAreaId;
		return this;
	}
	public ProductListCommand setOutputChannelId(Long outputChannelId) {
		this.outputChannelId = outputChannelId;
		return this;
	}
	public ProductListCommand setUiLanguage(String uiLanguage) {
		this.uiLanguage = uiLanguage;
		return this;
	}
	public ProductListCommand setLanguage(String language) {
		this.language = language;
		return this;
	}
	public ProductListCommand setFilterString(String filterString) {
		this.filterString = filterString;
		return this;
	}
	public ProductListCommand setPaging(Paging paging) {
		this.paging = paging;
		return this;
	}
}
