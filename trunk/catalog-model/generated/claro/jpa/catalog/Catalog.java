package claro.jpa.catalog;

import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Catalog {
    private Long id;
    private String name;
    private Collection<Item> items;
    private Category root;
    private Collection<OutputChannel> outputChannels;
    private Collection<Template> templates;
    private List<Language> languages;

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

    public Collection<Item> getItems() {
        if (items == null) {
            items = new ArrayList<Item>();
        }
        return items;
    }

    public void setItems(Collection<Item> value) {
        this.items = value;
    }

    public Category getRoot() {
        return root;
    }

    public void setRoot(Category value) {
        this.root = value;
    }

    public Collection<OutputChannel> getOutputChannels() {
        if (outputChannels == null) {
            outputChannels = new ArrayList<OutputChannel>();
        }
        return outputChannels;
    }

    public void setOutputChannels(Collection<OutputChannel> value) {
        this.outputChannels = value;
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

    public List<Language> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<Language>();
        }
        return languages;
    }

    public void setLanguages(List<Language> value) {
        this.languages = value;
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