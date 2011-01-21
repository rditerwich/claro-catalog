package claro.catalog.data;

import java.io.Serializable;

public class MediaValue implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Long propertyValueId;
	public String mimeType;
	public String filename;
	
	public MediaValue() {
	}
	
	public MediaValue(Long propertyValueId, String mimeType, String filename) {
		this.propertyValueId = propertyValueId;
		this.mimeType = mimeType;
		this.filename = filename;
	}
}
