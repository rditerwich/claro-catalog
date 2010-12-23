package claro.catalog.command.items;

import easyenterprise.lib.command.Command;

public class ItemDetailsCommand implements Command<ItemDetailsCommandResult>{

	private static final long serialVersionUID = 1L;
	
	private Long catalogId;
	private String language;
	private Long item;
	private Long outputChannel;
	private Long stagingArea;
	
	public Long getCatalogId() {
		return catalogId;
	}
	
	public ItemDetailsCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}
	
	public Long getItem() {
		return item;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public Long getOutputChannel() {
		return outputChannel;
	}
	
	public Long getStagingArea() {
		return stagingArea;
	}
	
	public ItemDetailsCommand setItem(Long item) {
		this.item = item;
		return this;
	}
	
	public ItemDetailsCommand setLanguage(String language) {
		this.language = language;
		return this;
	}
	
	public ItemDetailsCommand setOutputChannel(Long outputChannel) {
		this.outputChannel = outputChannel;
		return this;
	}
	 
	public ItemDetailsCommand setStagingArea(Long stagingArea) {
		this.stagingArea = stagingArea;
		return this;
	}
}
