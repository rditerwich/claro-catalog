package claro.catalog.data;

import java.io.Serializable;

import claro.jpa.catalog.PropertyType;

import com.google.common.base.Objects;

import easyenterprise.lib.util.SMap;

public class PropertyInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Long ownerItemId;
	public Long propertyId;
	private String type;
	public boolean isMany = false;
	public boolean isDangling = false;
	public SMap<String, String> labels = SMap.empty();
	public SMap<Integer, SMap<String, String>> enumValues = SMap.empty();
	
	public PropertyType getType() {
		return type != null? PropertyType.valueOf(type) : null;
	}
	
	public void setType(PropertyType type) {
		if (type != null) {
			this.type = type.name();
		} else {
			this.type = null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PropertyInfo) {
			return Objects.equal(this.propertyId, ((PropertyInfo)obj).propertyId);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(propertyId);
	}
	
	@Override
	public String toString() {
		return "PropertyInfo(" + propertyId + ")";
	}
}
