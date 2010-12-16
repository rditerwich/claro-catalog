package claro.jpa.shop;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.Template;

public class Shop extends OutputChannel {
    private String name;
    private String urlPrefix;
    private String defaultLanguage;
    private Collection<Navigation> navigation;
    private Collection<Property> excludedProperties;
    private Collection<Item> excludedItems;
    private Collection<Promotion> promotions;
    private Collection<Template> templates;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String value) {
        this.urlPrefix = value;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String value) {
        this.defaultLanguage = value;
    }

    public Collection<Navigation> getNavigation() {
        if (navigation == null) {
            navigation = new ArrayList<Navigation>();
        }
        return navigation;
    }

    public void setNavigation(Collection<Navigation> value) {
        this.navigation = value;
    }

    public Collection<Property> getExcludedProperties() {
        if (excludedProperties == null) {
            excludedProperties = new ArrayList<Property>();
        }
        return excludedProperties;
    }

    public void setExcludedProperties(Collection<Property> value) {
        this.excludedProperties = value;
    }

    public Collection<Item> getExcludedItems() {
        if (excludedItems == null) {
            excludedItems = new ArrayList<Item>();
        }
        return excludedItems;
    }

    public void setExcludedItems(Collection<Item> value) {
        this.excludedItems = value;
    }

    public Collection<Promotion> getPromotions() {
        if (promotions == null) {
            promotions = new ArrayList<Promotion>();
        }
        return promotions;
    }

    public void setPromotions(Collection<Promotion> value) {
        this.promotions = value;
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
        if (other instanceof Shop) {
            Shop otherShop = (Shop) other;
            return super.equals(otherShop);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}