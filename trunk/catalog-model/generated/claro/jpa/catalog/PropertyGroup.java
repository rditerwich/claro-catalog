package claro.jpa.catalog;

import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;

public class PropertyGroup {
    private Long id;
    private Collection<Label> labels;
    private Item item;
    private Collection<Property> properties;

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

    public Item getItem() {
        return item;
    }

    public void setItem(Item value) {
        this.item = value;
    }

    public Collection<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return properties;
    }

    public void setProperties(Collection<Property> value) {
        this.properties = value;
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