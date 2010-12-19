package claro.catalog.data;

import java.io.Serializable;

import claro.jpa.catalog.PropertyType;
import easyenterprise.lib.util.SMap;

public class PropertyInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Long ownerItemId;
	public Long propertyId;
	public PropertyType type;
	public boolean isMany = false;
	public boolean isDangling = false;
	public SMap<String, String> labels = SMap.empty();
	public SMap<Integer, SMap<String, String>> enumValues = SMap.empty();
}
