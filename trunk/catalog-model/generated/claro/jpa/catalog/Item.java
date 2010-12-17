package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public class Item implements Serializable {
    private Long id;
    private Catalog catalog;
    private Collection<ParentChild> parents;
    private Collection<ParentChild> children;
    private Collection<PropertyGroup> propertyGroups;
    private Collection<Property> properties;
    private Collection<PropertyValue> propertyValues;
    private Collection<Template> templates;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog value) {
        this.catalog = value;
    }

    public Collection<ParentChild> getParents() {
        if (parents == null) {
            parents = new ArrayList<ParentChild>();
        }
        return parents;
    }

    public void setParents(Collection<ParentChild> value) {
        this.parents = value;
    }

    public Collection<ParentChild> getChildren() {
        if (children == null) {
            children = new ArrayList<ParentChild>();
        }
        return children;
    }

    public void setChildren(Collection<ParentChild> value) {
        this.children = value;
    }

    public Collection<PropertyGroup> getPropertyGroups() {
        if (propertyGroups == null) {
            propertyGroups = new ArrayList<PropertyGroup>();
        }
        return propertyGroups;
    }

    public void setPropertyGroups(Collection<PropertyGroup> value) {
        this.propertyGroups = value;
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

    public Collection<PropertyValue> getPropertyValues() {
        if (propertyValues == null) {
            propertyValues = new ArrayList<PropertyValue>();
        }
        return propertyValues;
    }

    public void setPropertyValues(Collection<PropertyValue> value) {
        this.propertyValues = value;
    }

    public Collection<Template> getTemplates() {
        if (templates == null) {
            templates = new ArrayList<Template>();
        }
        return templates;
    }

    public void setTemplates(Collection<Template> value) {
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