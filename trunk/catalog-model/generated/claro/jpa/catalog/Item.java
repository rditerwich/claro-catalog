package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Item implements Serializable {
    private Long id = 0l;
    private Catalog catalog;
    private List<ParentChild> parents;
    private List<ParentChild> children;
    private List<Property> properties;
    private List<PropertyValue> propertyValues;
    private List<Template> templates;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog value) {
        this.catalog = value;
    }

    public List<ParentChild> getParents() {
        if (parents == null) {
            parents = new ArrayList<ParentChild>();
        }
        return parents;
    }

    public void setParents(List<ParentChild> value) {
        this.parents = value;
    }

    public List<ParentChild> getChildren() {
        if (children == null) {
            children = new ArrayList<ParentChild>();
        }
        return children;
    }

    public void setChildren(List<ParentChild> value) {
        this.children = value;
    }

    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return properties;
    }

    public void setProperties(List<Property> value) {
        this.properties = value;
    }

    public List<PropertyValue> getPropertyValues() {
        if (propertyValues == null) {
            propertyValues = new ArrayList<PropertyValue>();
        }
        return propertyValues;
    }

    public void setPropertyValues(List<PropertyValue> value) {
        this.propertyValues = value;
    }

    public List<Template> getTemplates() {
        if (templates == null) {
            templates = new ArrayList<Template>();
        }
        return templates;
    }

    public void setTemplates(List<Template> value) {
        this.templates = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Item) {
            Item otherItem = (Item) other;
            if (this.id == null) {
                if (otherItem.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherItem.id)) {
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