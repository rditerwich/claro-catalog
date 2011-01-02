package claro.catalog.command;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandValidationException;

public class RootPropertiesCommand implements Command<RootPropertiesCommandResult> {
	private static final long serialVersionUID = 1L;
	
	private Long catalogId;
	
	public void checkValid() throws CommandValidationException {
	}

	public Long getCatalogId() {
		return catalogId;
	}
	
	public RootPropertiesCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}
}
