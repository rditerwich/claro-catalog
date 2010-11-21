package claro.catalog;

import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyValue;
import easyenterprise.lib.util.SMap;

public class PropertyData {

	public SMap<String, PropertyValue> values;
	public SMap<String, PropertyValue> derivedValues;
	public SMap<OutputChannel, SMap<String, PropertyValue>> derivedOutputChannelValues;
	public SMap<OutputChannel, SMap<String, PropertyValue>> outputChannelSpecificValues;
	public SMap<Alternate, SMap<String, PropertyValue>> alternateValues;
	public SMap<ImportSource, SMap<String, PropertyValue>> importSourceValues;
}
	