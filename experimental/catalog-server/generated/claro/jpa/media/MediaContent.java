package claro.jpa.media;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class MediaContent implements Serializable {
    private Long id;
    private String mimeType = "";
    private String hash = "";
    private byte[] data;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String value) {
        if (value == null) {
            value = "";
        }
        this.mimeType = value;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String value) {
        if (value == null) {
            value = "";
        }
        this.hash = value;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] value) {
        this.data = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MediaContent) {
            MediaContent otherMediaContent = (MediaContent) other;
            if (this.id == null) {
                if (otherMediaContent.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherMediaContent.id)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id  == null? 0 : id .hashCode());
        return result;
    }

}