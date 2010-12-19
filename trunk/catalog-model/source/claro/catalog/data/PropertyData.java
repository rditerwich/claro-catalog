package claro.catalog.data;

import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.util.SMap;

public class PropertyData {
	public SMap<OutputChannel, SMap<String, Object>> values;
	public SMap<StagingArea, SMap<OutputChannel, SMap<String, Object>>> effectiveValues;
	
	public SMap<OutputChannel, SMap<ImportSource, SMap<String, Object>>> importSourceValues;
}
	