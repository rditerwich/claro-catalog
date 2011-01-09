package claro.catalog.data;

import java.io.Serializable;

import com.google.common.base.Objects;

import easyenterprise.lib.util.SMap;

public class PropertyGroupInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public Long propertyGroupId;
	public SMap<String, String> labels = SMap.empty();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PropertyGroupInfo) {
			return Objects.equal(this.propertyGroupId, ((PropertyGroupInfo)obj).propertyGroupId);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(propertyGroupId);
	}
}
