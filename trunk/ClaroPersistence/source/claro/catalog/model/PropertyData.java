package claro.catalog.model;

import java.util.Map;

import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.Supplier;

public class PropertyData {

	Map<String, PropertyValue> values;
	Map<String, PropertyValue> derivedValues;
	Map<OutputChannel, Map<String, PropertyValue>> derivedOutputChannelValues;
	Map<OutputChannel, Map<String, PropertyValue>> outputChannelSpecificValues;
	Map<Supplier, Map<String, PropertyValue>> supplierValues;
	Map<ImportSource, Map<String, PropertyValue>> importSourceValues;
}
	