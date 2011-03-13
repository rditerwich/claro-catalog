package claro.jpa.media;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class MediaTag implements Serializable {
    private Long id;
    private Media media;
    private String tag = "";

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media value) {
        this.media = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String value) {
        if (value == null) {
            value = "";
        }
        this.tag = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MediaTag) {
            MediaTag otherMediaTag = (MediaTag) other;
            if (this.id == null) {
                if (otherMediaTag.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherMediaTag.id)) {
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