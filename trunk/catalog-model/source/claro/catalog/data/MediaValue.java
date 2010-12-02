package claro.catalog.data;

import java.io.Serializable;

public class MediaValue implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Long propertyValueId;
	public final String mimeType;
	public final String filename;

	public MediaValue(Long propertyValueId, String mimeType, String filename) {
		this.propertyValueId = propertyValueId;
		this.mimeType = mimeType;
		this.filename = filename;
	}
}
