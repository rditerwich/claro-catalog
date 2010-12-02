package claro.catalog.command;

import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.util.SMap;

public class ProductListCommandResult implements CommandResult {

	private static final long serialVersionUID = 1L;

	public SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products;
}
