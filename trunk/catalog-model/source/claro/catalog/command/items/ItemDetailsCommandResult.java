package claro.catalog.command.items;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.util.SMap;

public class ItemDetailsCommandResult implements CommandResult {

	private static final long serialVersionUID = 1L;
	
	public SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyData;
	public SMap<PropertyInfo, PropertyData> danglingPropertyData;
	
}
