package claro.catalog.command;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class RootDataCommand implements Command<RootDataCommand.Result> {
	private static final long serialVersionUID = 1L;
	
	private Long catalogId;
	
	public void checkValid() throws CommandValidationException {
		validate (catalogId != null);
	}

	public Long getCatalogId() {
		return catalogId;
	}
	
	public RootDataCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}
	
	public SerializableTypes dummy;
	public static class Result implements CommandResult {

		private static final long serialVersionUID = 1L;

		public SerializableTypes dummy;
		public PropertyGroupInfo generalGroup;
		public Long rootCategory;
		public SMap<String, String> rootCategoryLabels;
		public SMap<String, PropertyInfo> rootProperties;
	}

}
