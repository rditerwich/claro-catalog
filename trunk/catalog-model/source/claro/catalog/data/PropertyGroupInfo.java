package claro.catalog.data;

import java.io.Serializable;

import easyenterprise.lib.util.SMap;

public class PropertyGroupInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public Long ownerItemid;
	public Long propertyGroupId;
	public SMap<String, String> labels = SMap.empty();
}
