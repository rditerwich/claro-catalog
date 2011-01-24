package claro.catalog.command;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;

public class RootDataCommand implements Command<RootDataCommand.Result> {
	private static final long serialVersionUID = 1L;
	
	private Long catalogId;
	
	public void checkValid() throws CommandValidationException {
	}

	public Long getCatalogId() {
		return catalogId;
	}
	
	public RootDataCommand setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
		return this;
	}
	
	public Money dummy;
	public static class Result implements CommandResult {

		private static final long serialVersionUID = 1L;

		public MediaValue dummy;
		public Money dummy2;
		public PropertyGroupInfo generalGroup;
		public Long rootCategory;
		public SMap<String, String> rootCategoryLabels;
		public SMap<String, PropertyInfo> rootProperties;
	}

}
