package claro.catalog;

import claro.catalog.util.SMap;
import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyValue;

public class PropertyData {

	SMap<String, PropertyValue> values;
	SMap<String, PropertyValue> derivedValues;
	SMap<OutputChannel, SMap<String, PropertyValue>> derivedOutputChannelValues;
	SMap<OutputChannel, SMap<String, PropertyValue>> outputChannelSpecificValues;
	SMap<Alternate, SMap<String, PropertyValue>> alternateValues;
	SMap<ImportSource, SMap<String, PropertyValue>> importSourceValues;
}
	