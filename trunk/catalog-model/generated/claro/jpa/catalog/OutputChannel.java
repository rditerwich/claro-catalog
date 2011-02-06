package claro.jpa.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OutputChannel implements Serializable {
    private Long id;
    private String name = "";
    private Catalog catalog;
    private String defaultLanguage;
    private List<Property> excludedProperties;
    private List<Item> excludedItems;

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
        if (value == null) {
            value = "";
        }
        this.name = value;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog value) {
        this.catalog = value;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String value) {
        this.defaultLanguage = value;
    }

    public List<Property> getExcludedProperties() {
        if (excludedProperties == null) {
            excludedProperties = new ArrayList<Property>();
        }
        return excludedProperties;
    }

    public void setExcludedProperties(List<Property> value) {
        this.excludedProperties = value;
    }

    public List<Item> getExcludedItems() {
        if (excludedItems == null) {
            excludedItems = new ArrayList<Item>();
        }
        return excludedItems;
    }

    public void setExcludedItems(List<Item> value) {
        this.excludedItems = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OutputChannel) {
            OutputChannel otherOutputChannel = (OutputChannel) other;
            if (this.id == null) {
                if (otherOutputChannel.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherOutputChannel.id)) {
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