package claro.catalog;

import java.util.Map;

import claro.catalog.model.PropertyData;
import claro.jpa.catalog.Property;

abstract class CommandResult {
	final String view;
	
}
public class ItemDetailsCommandResult {

	Map<Property, PropertyData> propertyData;
	Map<Property, PropertyData> danglingPropertyData;
	
}
