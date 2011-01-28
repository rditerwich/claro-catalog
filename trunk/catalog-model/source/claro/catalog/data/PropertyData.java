package claro.catalog.data;

import java.io.Serializable;

import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Source;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.util.SMap;

public class PropertyData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The item that is assigned this property to it's group.
	 */
	public Long groupAssignmentItemId;
	public SMap<String, String> groupItemNameLabels;

	public SMap<OutputChannel, SMap<String, Object>> values;
	public SMap<StagingArea, SMap<OutputChannel, SMap<String, Object>>> effectiveValues;
	
	public SMap<OutputChannel, SMap<Source, SMap<String, Object>>> sourceValues;
}
	