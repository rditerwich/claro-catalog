package claro.catalog.data;

import java.io.Serializable;

import com.google.common.base.Objects;

public class MediaValue implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final MediaValue empty = MediaValue.create(null, null, null);
	
	public Long mediaContentId;
	public String mimeType;
	public String name;

	@Override
	public String toString() {
		return "MediaValue(" + mediaContentId + "," + mimeType + "," + name + ")";
	}
	
	public boolean isEmpty() {
		return mediaContentId == null;
	}

	public static boolean isEmpty(Object obj) {
		return !(obj instanceof MediaValue) || ((MediaValue) obj).isEmpty();
	}

	public static MediaValue create(Long mediaContentId, String mimeType, String name) {
		if (mediaContentId == null && mimeType == null && name == null && empty != null) {
			return empty;
		}
		MediaValue result = new MediaValue();
		result.mediaContentId = mediaContentId;
		result.mimeType = mimeType;
		result.name = name;
		
		return result;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(mediaContentId, mimeType, name);
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof MediaValue) {
			MediaValue other = (MediaValue) obj;
			return Objects.equal(mediaContentId, other.mediaContentId)
				  && Objects.equal(mimeType, other.mimeType)
				  && Objects.equal(name, other.name);
		}
		return false;
	}
}
