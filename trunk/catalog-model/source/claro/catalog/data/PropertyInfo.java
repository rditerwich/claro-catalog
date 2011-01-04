package claro.catalog.data;

import java.io.Serializable;

import org.hsqldb.lib.tar.PIFData;

import com.google.common.base.Objects;

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
}
