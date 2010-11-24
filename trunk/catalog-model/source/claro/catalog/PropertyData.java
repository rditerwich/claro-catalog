package claro.catalog;

import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.util.SMap;

public class PropertyData {

	public SMap<OutputChannel, SMap<String, PropertyValue>> values;
	public SMap<OutputChannel, SMap<String, PropertyValue>> effectiveValues;
	public SMap<StagingArea, SMap<String, PropertyValue>> stagingAreaValues;
	public SMap<ImportSource, SMap<String, PropertyValue>> importSourceValues;
}
	