package claro.jpa.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Catalog implements Serializable {
    private Long id;
    private String name;
    private List<Item> items;
    private Category root;
    private List<OutputChannel> outputChannels;
    private List<Template> templates;
    private List<Language> languages;
    private List<PropertyGroup> propertyGroups;

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

    public List<Item> getItems() {
        if (items == null) {
            items = new ArrayList<Item>();
        }
        return items;
    }

    public void setItems(List<Item> value) {
        this.items = value;
    }

    public Category getRoot() {
        return root;
    }

    public void setRoot(Category value) {
        this.root = value;
    }

    public List<OutputChannel> getOutputChannels() {
        if (outputChannels == null) {
            outputChannels = new ArrayList<OutputChannel>();
        }
        return outputChannels;
    }

    public void setOutputChannels(List<OutputChannel> value) {
        this.outputChannels = value;
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

    public List<Language> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<Language>();
        }
        return languages;
    }

    public void setLanguages(List<Language> value) {
        this.languages = value;
    }

    public List<PropertyGroup> getPropertyGroups() {
        if (propertyGroups == null) {
            propertyGroups = new ArrayList<PropertyGroup>();
        }
        return propertyGroups;
    }

    public void setPropertyGroups(List<PropertyGroup> value) {
        this.propertyGroups = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Catalog) {
            Catalog otherCatalog = (Catalog) other;
            if (this.id == null) {
                if (otherCatalog.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherCatalog.id)) {
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