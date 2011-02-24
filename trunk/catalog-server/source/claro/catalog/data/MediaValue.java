package claro.catalog.data;

import java.io.Serializable;

public class MediaValue implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Long propertyValueId;
	public String mimeType;
	public String filename;
	transient public byte[] content;
	
	@Override
	public String toString() {
		return "MediaValue(" + propertyValueId + "," + mimeType + "," + filename + ")";
	}
	
	public static boolean mediaIsNull(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof MediaValue) {
			MediaValue v = (MediaValue) obj;
			return v.mimeType == null && v.filename == null && v.content == null;
		}
		
		return false;
	}
	public static MediaValue create(Long propertyValueId, String mimeType, String filename) {
		return create(propertyValueId, mimeType, filename, null);
	}
	public static MediaValue create(Long propertyValueId, String mimeType, String filename, byte[] content) {
		MediaValue result = new MediaValue();
		result.propertyValueId = propertyValueId;
		result.mimeType = mimeType;
		result.filename = filename;
		result.content = content;
		
		return result;
	}
}
