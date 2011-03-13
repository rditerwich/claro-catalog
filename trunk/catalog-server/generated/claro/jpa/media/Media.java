package claro.jpa.media;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import claro.jpa.catalog.Catalog;

@SuppressWarnings("serial")
public class Media implements Serializable {
    private Long id;
    private String name;
    private MediaContent content;
    private List<MediaTag> tags;
    private Catalog catalog;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public MediaContent getContent() {
        return content;
    }

    public void setContent(MediaContent value) {
        this.content = value;
    }

    public List<MediaTag> getTags() {
        if (tags == null) {
            tags = new ArrayList<MediaTag>();
        }
        return tags;
    }

    public void setTags(List<MediaTag> value) {
        this.tags = value;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog value) {
        this.catalog = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Media) {
            Media otherMedia = (Media) other;
            if (this.id == null) {
                if (otherMedia.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherMedia.id)) {
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