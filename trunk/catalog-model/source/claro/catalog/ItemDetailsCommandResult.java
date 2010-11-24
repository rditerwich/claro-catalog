package claro.catalog;

import java.util.Map;

import claro.jpa.catalog.Property;

public class ItemDetailsCommandResult {

	Map<Property, PropertyData> propertyData;
	Map<Property, PropertyData> danglingPropertyData;
	
}
