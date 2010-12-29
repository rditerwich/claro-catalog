package claro.catalog.command;

import easyenterprise.lib.command.Command;

public class RootPropertiesCommand implements Command<RootPropertiesCommandResult> {
	private static final long serialVersionUID = 1L;
	
	private Long catalogId;
	
	public Long getCatalogId() {
		return catalogId;
	}
	
	public RootPropertiesCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}
}
