package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class PropertyGroup implements Serializable {
    private Long id;
    private Collection<Label> labels;
    private Catalog catalog;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Collection<Label> getLabels() {
        if (labels == null) {
            labels = new ArrayList<Label>();
        }
        return labels;
    }

    public void setLabels(Collection<Label> value) {
        this.labels = value;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog value) {
        this.catalog = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PropertyGroup) {
            PropertyGroup otherPropertyGroup = (PropertyGroup) other;
            if (this.id == null) {
                if (otherPropertyGroup.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherPropertyGroup.id)) {
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