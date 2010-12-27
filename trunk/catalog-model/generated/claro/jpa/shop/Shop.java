package claro.jpa.shop;

import java.io.Serializable;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Template;

@SuppressWarnings("serial")
public class Shop extends OutputChannel implements Serializable {
    private String urlPrefix;
    private Collection<Navigation> navigation;
    private Collection<Promotion> promotions;
    private Collection<Template> templates;

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String value) {
        this.urlPrefix = value;
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