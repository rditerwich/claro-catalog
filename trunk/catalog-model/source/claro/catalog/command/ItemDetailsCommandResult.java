package claro.catalog.command;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.util.SMap;

public class ItemDetailsCommandResult implements CommandResult {

	private static final long serialVersionUID = 1L;
	
	public SMap<PropertyInfo, PropertyData> propertyData;
	public SMap<PropertyInfo, PropertyData> danglingPropertyData;
	
}
