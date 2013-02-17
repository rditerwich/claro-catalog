package claro.jpa.media;

import java.lang.Override;
import java.lang.String;

public class MediaContentKey {

    private String mimeType;
    private String contentHash;

    public MediaContentKey() {
    }

    public MediaContentKey(String mimeType, String contentHash) {
        this.mimeType = mimeType;
        this.contentHash = contentHash;
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String value) {
        mimeType = value;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String value) {
        contentHash = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MediaContentKey) {
            return equals((MediaContentKey)other);
        }
        return false;
    }

    public boolean equals(MediaContentKey other) {
        if (this.mimeType == null) {
            if (other.mimeType != null) {
                return false;
            }
        } 
        else if (!this.mimeType.equals(other.mimeType)) {
            return false;
        }

        if (this.contentHash == null) {
            if (other.contentHash != null) {
                return false;
            }
        } 
        else if (!this.contentHash.equals(other.contentHash)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (mimeType  == null? 0 : mimeType .hashCode());
        result = 37 * result + (contentHash  == null? 0 : contentHash .hashCode());
        return result;
    }

}