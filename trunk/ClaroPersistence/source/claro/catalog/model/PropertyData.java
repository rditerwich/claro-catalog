package claro.catalog.model;

import java.util.Map;

import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyValue;

public class PropertyData {

	public Map<String, PropertyValue> values;
	public Map<String, PropertyValue> derivedValues;
	public Map<OutputChannel, Map<String, PropertyValue>> derivedOutputChannelValues;
	public Map<OutputChannel, Map<String, PropertyValue>> outputChannelSpecificValues;
	public Map<Alternate, Map<String, PropertyValue>> alternateValues;
	public Map<ImportSource, Map<String, PropertyValue>> importSourceValues;
}
	